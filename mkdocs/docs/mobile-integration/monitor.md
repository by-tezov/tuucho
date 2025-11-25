# Monitors

Tuucho provides monitors. They are meant **for debugging**, when you need visibility into what Tuucho is doing internally.

---

## Coroutine Exception Monitor

Tuucho makes heavy use of coroutines and has several dedicated jobs (database, navigation, rendering, etc.).  

You can attach a monitor to log exceptions happening inside these jobs. this monitor sits at the root of job execution and cannot miss any exception.
It can be useful to have a better understanding of any issue.

```kotlin
class LoggerCoroutineExceptionMonitor(
    private val systemInformation: SystemInformationProtocol
) : CoroutineExceptionMonitor {
    
    override fun process(
        context: CoroutineExceptionMonitor.Context
    ) {
        with(context) {
            println("THREAD ${systemInformation.currentThreadName()}")
            println("COROUTINE, $throwable $id:$name ")
        }
    }
}
```

---

## Interaction Lock Monitor

Tuucho has a lock mechanism for shared resources.

Examples:
- During navigation with transitions (to avoid spamming navigation).
- Screen lock (to prevent interactions while navigation is in progress).

This monitor allows you to observe all lock events.

```kotlin
class LoggerInteractionLockMonitor(
    private val systemInformation: SystemInformationProtocol
) : InteractionLockMonitor {

    override fun process(context: InteractionLockMonitor.Context) {
        with(context) {
            println("THREAD ${systemInformation.currentThreadName()}")
            println("LOCK:$event $requester - ${if (lockTypes.isEmpty()) "nothing" else lockTypes.toString()} ")
        }
    }
}
```

---

## Registering Monitors in Koin

Monitors must be supplied to Koin under **ModuleGroupDomain.Main**.

```kotlin
module(ModuleGroupDomain.Main) {

        factory<CoroutineExceptionMonitor> {
            LoggerCoroutineExceptionMonitor(
                logger = get(),
                systemInformation = get()
            )
        }

        factory<InteractionLockMonitor> {
            LoggerInteractionLockMonitor(
                logger = get(),
                systemInformation = get()
            )
        }
}
```

---

## SystemInformationProtocol

`SystemInformationProtocol` is provided by Tuucho. It gives access to information such as the **current thread**, helping you inspect how Tuucho dispatches work internally.

---

# Additional Monitoring Options

Monitoring in Tuucho is not limited to the built-in monitors. You can observe almost every part of the pipeline by using the right extension point.

### Monitor Actions
You can make a logger middleware to monitor actions.  
More details here:  
[mobile-integration/action](action.md)

### Monitor Network Requests
You can add a logger interceptor to track network requests and responses.  
More details here:  
[mobile-integration/interceptor](interceptor.md)

### Monitor Navigation, SendData, View Updates
You can create a logger middleware for each of these flows:
- Navigation
- SendData communication
- Contextual view update

More details here:  
[mobile-integration/middleware](middleware.md)