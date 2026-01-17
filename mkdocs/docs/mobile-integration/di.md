# Dependency Injection in Tuucho (Koin Integration)

Tuucho uses **Koin** as its dependency injection framework.

Tuucho runs inside **its own isolated Koin context**, so it never conflicts with the application's own Koin instance. You can safely use Koin in your app while Tuucho uses its internal context.

Thanks to Koin, you can extend Tuucho by providing:

- Configuration — see: `mobile-integration/config.md`
- Interceptors — see: `mobile-integration/interceptor.md`
- Middleware — see: `mobile-integration/middleware.md`
- Monitors — see: `mobile-integration/monitor.md`
- Actions — see: `mobile-integration/action.md`

Tuucho organizes modules using **module context** and its own `ModuleProtocol`. Each feature documents which module group it belongs to.

---

## Supplying Configuration (Network Example)

```kotlin
module(ModuleContextCore.Main) {

    factory<NetworkRepositoryModule.Config> {
        object : NetworkRepositoryModule.Config {
            override val timeoutMillis = ...
            override val version = ...
            override val baseUrl = ...
        }
    }
}
```

---

# Recommended Structure for KMM Projects

A single expect/actual pair handles dependency injection per Gradle module.

---

## commonMain

```kotlin
expect fun SystemSharedModules.platformInvoke(): List<KoinMass>

object SystemSharedModules {

    fun invoke(): List<KoinMass> = listOf(
        module(ModuleContextCore.Main) {
            single { Logger(exceptionVerbose = false) }
        },
        MonitorModule.invoke(),
        InteractionModule.invoke(),
        InterceptorModule.invoke(),
        MiddlewareModule.invoke()
    ) + platformInvoke()
}
```

---

## Platform Side (Android or iOS)

```kotlin
internal actual fun SystemSharedModules.platformInvoke(): List<KoinMass> = listOf(
    NetworkModuleAndroid.invoke(),
    ConfigModuleAndroid.invoke()
)
```

---

### Example: OkHttp Engine

```kotlin
internal object NetworkModuleAndroid {

    fun invoke() = module(ModuleContextCore.Main) {
        factory<HttpClientEngineFactory<*>> {
            OkHttp
        }
    }
}
```

Tuucho allows you to supply your own **Ktor HttpClient engine**.

- On **Android**, you can provide your own **OkHttp** instance.
- On **iOS**, you can provide your own **Darwin** engine.

If you do **not** supply a custom engine, Tuucho automatically creates and manages its **own default engine instance**.

---

For more detail about avoiding expect/actual bloat with Koin in KMM:  
https://medium.com/itnext/ditch-the-expect-actual-bloat-embrace-koin-for-kmm-6ca2392b24be

---