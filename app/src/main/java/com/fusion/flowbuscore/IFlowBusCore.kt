package com.fusion.flowbuscore

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * author: Zfusion
 */
interface IFlowBusCore {

    fun <T: Any> post(
        scope: CoroutineScope? = null,
        context: CoroutineContext = Dispatchers.Default,
        key: String,
        value: T
    )

    fun <T: Any> postSticky(
        scope: CoroutineScope? = null,
        context: CoroutineContext = Dispatchers.Default,
        key: String,
        value: T
    )

    suspend fun <T: Any> postDelay(
        key: String,
        value: T,
        timeMillis: Long
    )





    fun <T: Any> observeEvent(
        lifecycle: Lifecycle,
        context: CoroutineContext = Dispatchers.Main.immediate,
        key: String,
        action: suspend CoroutineScope.(T) -> Unit
    )

    fun <T: Any> observeStickyEvent(
        lifecycle: Lifecycle,
        context: CoroutineContext = Dispatchers.Main.immediate,
        key: String,
        action: suspend CoroutineScope.(T) -> Unit
    )

}