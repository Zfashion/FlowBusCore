package com.fusion.flowbuslib

import android.util.ArrayMap
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

/**
 * author: Zfusion
 */
class FlowBusCore: IFlowBusCore {

    /**
     * 可以在同层级、非同层、延时发送单个消息
     * 可以发送指定的消息
     * 可发送多个消息
     */
    companion object {
        private const val TAG = "FlowBusCore"

        @JvmStatic
        private val INSTANCE: FlowBusCore by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { FlowBusCore() }

        fun getInstance() = INSTANCE

    }

    private val eventMessagePool = ArrayMap<String, MutableSharedFlow<out Any>>()

    private val eventStickyMessagePool = ArrayMap<String, MutableSharedFlow<out Any>>()

    /**
     * 可提供作用域，调用flow.emit
     * 发送消息
     */
    override fun <T: Any> post(
        scope: CoroutineScope?,
        context: CoroutineContext,
        key: String,
        value: T
    ) {
        if (scope == null) {
            dealTryEmit(false, key, value)
        } else {
            scope.launch(context) {
                dealEmit(false, key, value)
            }
        }
    }

    /**
     * 需提供作用域，调用flow.emit
     * 发送粘性消息
     */
    override fun <T: Any> postSticky(
        scope: CoroutineScope?,
        context: CoroutineContext,
        key: String,
        value: T
    ) {
        if (scope == null) {
            dealTryEmit(true, key, value)
        } else {
            scope.launch(context) {
                dealEmit(true, key, value)
            }
        }
    }

    /**
     * 挂起api,需自己启动一个协程方可调用
     * 延时发送消息
     */
    override suspend fun <T: Any> postDelay(
        key: String,
        value: T,
        timeMillis: Long
    ) {
        delay(timeMillis)
        dealEmit(false, key, value)
    }


    /**
     * 消息订阅
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> observeEvent(
        lifecycle: Lifecycle,
        context: CoroutineContext,
        key: String,
        action: suspend CoroutineScope.(T) -> Unit
    ) {
        val lifecycleObserverWrapper = FlowBusEventObserver(false, key, context, action)
        lifecycle.addObserver(lifecycleObserverWrapper)

        /*val flow = takeFlowWithKey<T>(false, key)
        lifecycle.coroutineScope.launch {
            flow
                .flowWithLifecycle(lifecycle)
                .onCompletion { Log.d(TAG, "collect completion!") }
                .cancellable()
                .catch { e -> Log.e(TAG, "collect exception=$e") }
                .flowOn(Dispatchers.Default)
                .collectLatest {
                    withContext(context) {
                        action(it)
                    }
                }
        }*/


        /*val job = lifecycleOwner.lifecycleScope.launch {
            //用来接收消息 flow 的子协程
            var collectJob: Job? = null

            //flow 与 LifecycleOwner 的生命周期绑定类对象
            var observer: DefaultLifecycleObserver? = null

            try {
                suspendCancellableCoroutine<Unit> { cont ->
                    //启动接收消息的子协程
                    collectJob = this.launch(Dispatchers.Default) {
                        while (true) {
                            ensureActive()

                            val flow = checkFlowWithKey<T>(false, key)
                            println("$TAG -> flow 存在：${flow != null}")
                            if (flow == null) continue

                            println("该flow存在，尝试执行收集项")

                            flow
                                .onEach {
                                    println("$TAG -> 接收到消息")
                                    //协程调度用户指定上下文
                                    withContext(context) {
                                        action(it as T)
                                    }
                                }
                                .onCompletion { println("发送完了") }
                                .catch { e -> println("$TAG observeEvent() -> Caught $e") }
                                .collect()
                        }
                    }

                    val mutex = Mutex()
                    //定义该生命周期绑定类对象
                    observer = FlowBusEventObserver ({
                        mutex.withLock {
                            println("$TAG -> 尝试取消子协程")
                            collectJob?.cancel()
                            collectJob = null
                        }
                    }, {
                        println("$TAG -> 恢复挂起点")
                        cont.resume(Unit)
                    })

                    println("$TAG -> 为当前lifecycleOwner添加监听")
                    lifecycleOwner.lifecycle.addObserver(observer as DefaultLifecycleObserver)
                }
            } finally {
                println("$TAG -> 销毁监听和子协程")
                collectJob?.cancel()
                observer?.let {
                    lifecycleOwner.lifecycle.removeObserver(it)
                }
            }
        }*/

    }

    /**
     * 粘性消息订阅
     */
    override fun <T: Any> observeStickyEvent(
        lifecycle: Lifecycle,
        context: CoroutineContext,
        key: String,
        action: suspend CoroutineScope.(T) -> Unit
    ) {
        val lifecycleObserverWrapper = FlowBusEventObserver(true, key, context, action)
        lifecycle.addObserver(lifecycleObserverWrapper)
    }




    private suspend fun <T: Any> dealEmit(isSticky: Boolean, key: String, value: T) {
        val flow = takeFlowWithKey<T>(isSticky, key)
        Log.d(TAG, "emit msg, key-$key, flow-${flow.hashCode()}")
        flow.emit(value)
    }

    private fun <T: Any> dealTryEmit(isSticky: Boolean, key: String, value: T) {
        val flow = takeFlowWithKey<T>(isSticky, key)
        Log.d(TAG, "try emit msg, key-$key, flow-${flow.hashCode()}")
        flow.tryEmit(value)
    }


    @Synchronized
    internal fun <T: Any> takeFlowWithKey(isSticky: Boolean, key: String): MutableSharedFlow<T> {
        val pool = getPool(isSticky)
        return if (pool.containsKey(key)) {
            val flow = pool[key]
            flow as? MutableSharedFlow<T> ?: createRecommendFlow(isSticky, key)
        } else {
            createRecommendFlow(isSticky, key)
        }
    }

    private fun <T: Any> checkFlowWithKey(isSticky: Boolean, key: String): MutableSharedFlow<T>? {
        return getPool(isSticky)[key] as? MutableSharedFlow<T>
    }

    private fun <T : Any> createRecommendFlow(isSticky: Boolean, key: String): MutableSharedFlow<T> {
        val sharedFlow = createDefaultFlow<T>(isSticky)
        if (isSticky) {
            eventStickyMessagePool[key] = sharedFlow
        } else {
            eventMessagePool[key] = sharedFlow
        }
        return sharedFlow
    }

    private fun getPool(isSticky: Boolean) =
        if (isSticky) eventStickyMessagePool else eventMessagePool

    private fun <T: Any> createDefaultFlow(isSticky: Boolean): MutableSharedFlow<T> =
        if (isSticky) MutableSharedFlow(replay = 1, 0) else MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE)



    inner class FlowBusEventObserver<T>(
        private val isSticky: Boolean,
        private val key: String,
        private val context: CoroutineContext,
        private val action: suspend CoroutineScope.(T) -> Unit): DefaultLifecycleObserver
    {
        private var flow: Flow<T>? = null
        private var collectJob: Job? = null

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            flow = takeFlowWithKey(isSticky, key)
            Log.d(TAG, "onCreate -> flow= ${flow.hashCode()}, FlowBusEventObserver= ${this.hashCode()}")
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            Log.d(TAG, "FlowBusEventObserver onResume")
            collectJob = flow?.onEach {
                        //协程调度用户指定上下文
                        withContext(context) {
                            action(it)
                        }
                    }
                    ?.onCompletion { Log.d(TAG, "${flow?.hashCode()} completion!") }
                    ?.cancellable()
                    ?.catch { e -> Log.e(TAG, "collect exception=$e") }
//                    ?.flowOn(Dispatchers.Default)
                    ?.launchIn(owner.lifecycleScope + Dispatchers.Default)
            /*collectJob?.invokeOnCompletion {
                Log.d(TAG,"collectJob invokeOnCompletion")
            }*/
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            Log.d(TAG, "onPause -> flow= ${flow.hashCode()}, FlowBusEventObserver= ${this.hashCode()}")
            collectJob?.cancel()
        }
    }

}