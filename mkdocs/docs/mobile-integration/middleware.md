# Middleware

Tuucho provides several middleware hooks that let you intercept and alter different parts of the system:
- 
- Navigation (forward, back & finish)
- SendData operations
- Load Image operations
- View updates (contextual rendering)

Middleware execute **in the exact order they are declared**, thanks to `bindOrdered`. All middleware must be supplied to **ModuleContextDomain.Middleware**.

---

## Navigation Middleware

Tuucho provides middleware for forward, backward and finish navigation.

```kotlin
object NavigationMiddleware {

    /* in navigation context, NavigateToUrlUseCase.Input is the target url 
        data class Input(
            val url: String
        ) 
    */

    fun interface ToUrl : MiddlewareProtocol<ToUrl.Context, Unit> {
        data class Context(
            val currentUrl: String?,
            val input: NavigateToUrlUseCase.Input,
            val onShadowerException: OnShadowerException?
        )
    }

    fun interface Back : MiddlewareProtocol<Back.Context, Unit> {
        data class Context(
            val currentUrl: String,
            val nextUrl: String?,
            val onShadowerException: OnShadowerException?
        )
    }

    fun interface Finish : MiddlewareProtocol<Unit>
}
```

### Forward Navigation — `NavigationMiddleware.ToUrl`

You can use these hooks to alter the behaviour before or after navigation. Typical cases:

- Redirect to login when catching unauthenticated exceptions
- Log analytics before entering a protected or specific area
- Apply custom logic before transition
- Handle async loading issues via `onShadowerException`
- Simple logging

```kotlin
class BeforeNavigateToUrlMiddleware() : NavigationMiddleware.ToUrl {

    override suspend fun process(
        context: NavigationMiddleware.ToUrl.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.ToUrl.Context, Unit>?,
    ) {
        // do something before
        
        runCatching {
            next.invoke(context.copy(
                onShadowerException = {
                    exception, context, replay ->
                    // do something is case of async failure while loading contextual data
                }
            ))
        }.onFailure {
            // do something is case of sync failure
        }
        
        // do something after
    }
}
```

Register forward navigation middleware:

```kotlin
module(ModuleContextDomain.Middleware) {
    factoryOf(::BeforeNavigateToUrlMiddleware) bindOrdered NavigationMiddleware.ToUrl::class
}
```

Make sure to use **bindOrdered**

---

### Back Navigation — `NavigationMiddleware.Back`

Use `NavigationMiddleware.Back` to intercept backward navigation.

```kotlin
class BeforeNavigateBackMiddleware() : NavigationMiddleware.Back {

    override suspend fun process(
        context: NavigationMiddleware.Back.Context,
        next: MiddlewareProtocol.Next<NavigationMiddleware.Back.Context, Unit>?,
    ) {
        runCatching {
            next.invoke(context.copy(
                onShadowerException = {
                        exception, context, replay ->
                    // do something is case of async failure while loading contextual data
                }
            ))
        }.onFailure {
            // do something is case of sync failure
        }

        // do something after
    }
}
```

Register it:

```kotlin
module(ModuleContextDomain.Middleware) {
    factoryOf(::BeforeNavigateBackMiddleware) bindOrdered NavigationMiddleware.Back::class
}
```

Make sure to use **bindOrdered**

---

### Finish Navigation — `NavigationMiddleware.Finish`

Use `NavigationMiddleware.Finish` to get inform that Finish use case has been trigger either by yourself on action or automatically when the navigation stack get empty.

That's your responsibility to honor this demand by finishing Tuucho and/or the activity / uiController / application.

```kotlin
class NavigationFinishPublisher(
    private val coroutineScopes: CoroutineScopesProtocol
) {
    private val _events = Notifier.Emitter<Unit>(
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    private val events get() = _events.createCollector

    @OptIn(TuuchoInternalApi::class)
    fun finish() {
        coroutineScopes.default.asyncOnCompletionThrowing {
            _events.emit(Unit)
        }
    }

    @OptIn(TuuchoInternalApi::class)
    fun onFinish(block: () -> Unit) {
        coroutineScopes.main.asyncOnCompletionThrowing {
            events.once { block() }
        }
    }
}

class NavigateFinishMiddleware(
    private val navigationFinishPublisher: NavigationFinishPublisher
) : NavigationMiddleware.Finish {

    override suspend fun process(
        context: Unit,
        next: MiddlewareProtocol.Next<Unit>?,
    ) {
        next?.invoke(Unit)
        navigationFinishPublisher.finish()
    }
}

// Then inside where you start Tuucho (code available in sample application) :
// Android
AppScreen(
    applicationModules = listOf(ApplicationModule.invoke(applicationContext)),
    koinExtension = {
        koin.get<NavigationFinishPublisher>().onFinish {
            koin.close()
            this@MainActivity.finish()
        }
    }
)

// iOS
let vc = KMPKitKt.uiView(koinExtension: { [weak self] koinApplication in
    guard let self = self else { return }
    let publisher = koinApplication.tuuchoKoinIos.get(clazz: NavigationFinishPublisher.self) as! NavigationFinishPublisher
    publisher.onFinish(block: {
        self.coordinator?.handleKoinClosed()
        koinApplication.tuuchoKoinIos.close()
        self.isKoinInitialized = false
        UIApplication.shared.perform(#selector(NSXPCConnection.suspend))
    })
})
```

Register it:

```kotlin
module(ModuleContextDomain.Middleware) {
    singleOf(::NavigationFinishPublisher)
    factoryOf(::NavigateFinishMiddleware) bindOrdered NavigationMiddleware.Finish::class
}
```

Make sure to use **bindOrdered**

---

## SendData Middleware

`SendDataMiddleware` is used when sending data to the server and receiving a response.  

For example:

- send form data and receive a server feedback

```kotlin
fun interface SendDataMiddleware : MiddlewareProtocol<Context, SendDataUseCase.Output> {
    data class Context(
        val input: SendDataUseCase.Input,
    )
}
```

`SendDataUseCase.Input`:

```kotlin
data class Input(
    val url: String,
    val jsonObject: JsonObject,
)
```

`url` → the endpoint  
`jsonObject` → the payload

`SendDataUseCase.Output`:

Returned output contains:

```kotlin
data class Output(
    val jsonObject: JsonObject?,
)
```

Format details can be found in [components-definition/form/](../components-definition/index.md)

```kotlin
class SendDataMiddleware() : SendDataMiddleware {

    override suspend fun process(
        context: SendDataMiddleware.Context,
        next: MiddlewareProtocol.Next<SendDataMiddleware.Context, SendDataUseCase.Output>?,
    ) = with(context.input) {

        // do something before
        
        ouput = next?.invoke(context)

        // do something after
        
        ouput
    }
}
```

Register it:

```kotlin
module(ModuleContextDomain.Middleware) {
    factoryOf(::SendDataMiddleware) bindOrdered SendDataMiddleware::class
}
```

---

## UpdateView Middleware

Triggered whenever the view updates dynamically:

- contextual data received
- internal triggers (state changes)
- server-driven UI updates

Allows altering UI data **before rendering**.

```kotlin
class UpdateViewMiddleware() : UpdateViewMiddleware {

    override suspend fun process(
        context: UpdateViewMiddleware.Context,
        next: MiddlewareProtocol.Next<UpdateViewMiddleware.Context, Unit>?,
    ) {
        with(context.input) {
            // do something before

            next?.invoke(context)

            // do something after
        }
    }
}
```

Input:

```kotlin
data class Input(
    val route: NavigationRoute.Url,
    val jsonObject: JsonObject,
)
```

`jsonObject` = Tuucho component / content / text, check the json documentation.

Register it:

```kotlin
module(ModuleGroupDomain.Middleware) {
    factoryOf(::UpdateViewMiddleware) bindOrdered UpdateViewMiddleware::class
}
```

---

Middleware are executed **in the order they are declared**, through `bindOrdered`.
