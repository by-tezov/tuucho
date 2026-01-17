# HTTP Interceptors

Tuucho allows you to add custom HTTP interceptors. An interceptor can:

- Add headers
- Modify the request
- Modify the response
- Implement retry logic
- Monitor or log anything
- Or completely bypass the network call

It’s entirely up to you. To add an interceptor, you implement the `HttpInterceptor` interface.

---

## Interceptor Structure

```kotlin
class HttpInterceptorImpl(
    private val config: InterceptorModule.Config
) : HttpInterceptor {

    override suspend fun process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>?
    ) = with(context.builder) {
        
        // Do something

        next?.invoke(context) // continue the chain
    }
}
```

The `process` function provides:

### **Context**
Gives access to the `HttpRequestBuilder`.

```kotlin
data class Context(
    val builder: HttpRequestBuilder,
)
```

### **Next**
A function you **need to call** to continue the processing chain.  
It returns an `HttpResponseData` that you must return from `process`.

You can:

- Call `next` **after** modifying the request
- Call `next` **before** modifying the response
- Or **not call next at all** to fully bypass the request  
  (in this case you return your own `HttpResponseData`)

---

## Registering Interceptors in Koin

All interceptors must be supplied to Koin under the **ModuleContextData.Interceptor** context.

```kotlin
module(ModuleContextData.Interceptor) {
    factoryOf(::HeadersHttpInterceptor) bindOrdered HttpInterceptor::class
}
```

**All interceptor instances must be bound using:**

```kotlin
bindOrdered HttpInterceptor::class
```

The order of declaration matters and Tuucho executes interceptors in the exact declared order.

---

## Example: Add Headers

```kotlin
class HeadersHttpInterceptor(
    private val config: InterceptorModule.Config
) : HttpInterceptor {
    
    override suspend fun process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>?
    ) = with(context.builder) {
        headers.append("platform", config.headerPlatform)
        next?.invoke(context)
    }
}
```

This interceptor simply adds a header.

---

## Example: Add Authorization Token for Protected Endpoints

Here is an example interceptor that injects a token for any endpoint under `auth/*`.

```kotlin
class HeaderHttpAuthorizationInterceptor(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val config: NetworkRepositoryModule.Config,
    private val getValueOrNullFromStore: GetValueOrNullFromStoreUseCase
) : HttpInterceptor {

    private val authRegex = Regex("^/auth(?:/.*)?$")

    override suspend fun process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>?
    ) = with(context.builder) {
        val route = url.toString()
            .removePrefix("${config.baseUrl}/")
            .removePrefix("${config.version}/")
            .let {
                when {
                    it.startsWith(config.healthEndpoint) -> it.removePrefix(config.healthEndpoint)
                    it.startsWith(config.resourceEndpoint) -> it.removePrefix(config.resourceEndpoint)
                    it.startsWith(config.sendEndpoint) -> it.removePrefix(config.sendEndpoint)
                    else -> it
                }
            }

        if (route.matches(authRegex)) {
            useCaseExecutor.await(
                useCase = getValueOrNullFromStore,
                input = GetValueOrNullFromStoreUseCase.Input(
                    key = "login-authorization".toKey()
                )
            )?.value?.value?.let { authorizationKey ->
                headers.append("authorization", "Bearer $authorizationKey")
            }
        }
        next?.invoke(context)
    }
}
```

Tuucho gives access to use cases injectable with Koin. Here, `GetValueOrNullFromStoreUseCase` retrieves a token stored through the command: 
```
store://key-value/save?{key}={value}&{key}={value}
```
More details are available in [object-definition/action/](../object-definition/action.md).  
All available use cases : [mobile-integration/usecases](usecases.md)

---

In [tuucho‑sample](https://github.com/by-tezov/tuucho), a token is stored when the user submits a login form. This interceptor retrieves it and attaches it to requests targeting the authentication area.

