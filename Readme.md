### 一、依赖

#### Step 1.Add the JitPack repository to your build file

Add it in your root settings.gradle at the end of repositories:

```groovy
dependencyResolutionManagement {
    ...
    repositories {
    	maven { 
    		url 'https://jitpack.io' 
    	}
    	...
    }
}
```

#### Step 2.Add the dependency

```groovy
dependencies {
    implementation("com.github.Zfashion:FlowBusCore:$TAG")
}
```

#### Step 3.Replace $TAG with the following version number

[![](https://jitpack.io/v/Zfashion/FlowBusCore.svg)](https://jitpack.io/#Zfashion/FlowBusCore)



### 二、使用

#### 1.发送普通消息

```kotlin
//第一步，先声明订阅者
FlowBusCore.getInstance().observeEvent<String>(lifecycle, key = "KEY") {
    //Do your job 
}

//第二步，定义发送
FlowBusCore.getInstance().post(key = "KEY", value = "Sunny Day!!!")
```

> 注意：普通发送的方式，必须先声明好订阅者，然后再进行发送，否则flow是不会执行的。

#### 2.发送粘性消息

```kotlin
//定义发送
FlowBusCore.getInstance().postSticky(key = "KEY", value = "Rainy day")

//声明订阅者
FlowBusCore.getInstance().observeStickyEvent<String>(lifecycle, key = "KEY") {
    //Do your job
}
```

> 提示：粘性发送的方式，可以不需要事先声明订阅者。

#### 3.发送延时消息

```kotlin
//声明订阅者
FlowBusCore.getInstance().observeEvent<String>(lifecycle, key = "KEY") {
    //Do your job
}

//需开启一个协程，在作用域内调用postDelay，因为该函数是suspend
${Scope}.launch() {
    FlowBusCore.getInstance().postDelay("KEY", "Cloudy", 2000)
}
```

> 提示：延时消息发送采用与发送普通消息一样的Flow配置，因为发送延时粘性消息是没有任何意义的 :-)



### 三、说明

（1）以上所有的订阅接收api，默认过程都是在异步流程中执行，执行的action对象默认协程调度到主线程；

（2）所有发送api可以指定作用域，上下文；不指定时，默认作用域为null，并且Flow发送走tryEmit()的api;

（3）所有订阅接收api必须提供一个Lifecycle，上下文可不指定，默认为主线程；

（4）订阅接收api跟随提供的Lifecycle的生命周期，所以该订阅接收可由上层决定生命周期范围；
