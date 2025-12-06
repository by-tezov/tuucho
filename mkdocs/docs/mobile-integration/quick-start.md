---
comments: true
---

# Quick Start

This guide shows how to quickly integrate **Tuucho** into a Kotlin Multiplatform project (iOS and Android). Tuucho is a server‑driven rendering engine published on Maven Central.

## 1 Add dependencies to the shared module

In your **shared module** (`commonMain`) you need the Tuucho core library plus its supporting libraries:

```kotlin
 commonMain.dependencies {
    implementation("com.tezov:tuucho.core:0.0.1-alpha22_2.2.21") // for kotlin 2.2.21     
    implementation("io.insert-koin:koin-core:4.1.1")     
    implementation("io.insert-koin:koin-compose:4.1.1")
    implementation("io.ktor:ktor-client-core:3.3.2")

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.ui)
 }
```

## 2 Create an `AppScreen` in `commonMain` in the shared module

Tuucho exposes a composable engine which loads its configuration from the server and renders the pages. The simplest way to start it is via an `AppScreen` composable:

```kotlin
@Composable
fun AppScreen(
    applicationModules: List<ModuleProtocol>,
) = TuuchoEngineStart(
        applicationModules = SystemSharedModules.invoke() + applicationModules,
        onStartUrl = "lobby/page-login"
    )
```

> You can make use of middleware navigation to preload component: 
 - [Config details](../config/index.md) to understand the config file format.
 - [Middleware details](middleware.md) to understand navigation middleware.

You can also check the [tuucho‑sample](https://github.com/by-tezov/tuucho) in the repository.

## 3 Supply configuration via Koin in the shared module

Tuucho needs configuration properties, like you server base url, endpoint, database file name.  Define a Koin module that provides a `SystemCoreDataModules.Config` instance.  In the shared module you can implement this interface with your own values:

```kotlin
object ConfigModule {

    fun invoke() = module(ModuleGroupCore.Main) {
        factory<NetworkRepositoryModule.Config> {
            object : NetworkRepositoryModule.Config {
                override val timeoutMillis = 5000
                override val version = "v1"
                override val baseUrl = "http://localhost:3000/"
                override val healthEndpoint = "health"
                override val resourceEndpoint = "resource"
                override val sendEndpoint = "send"
            }
        }
    }
}
```

The core module defines the `Config` interface for `NetworkRepositoryModule`.  

> The sample application uses `BuildKonfig` to generate these values per platform.

## 4 Android integration

On Android you have to provide the application `Context` so that Tuucho can access platform services. Define an `ApplicationModuleDeclaration` that injects the context using Koin:

First you need the to add the dependencies

```kotlin
 dependencies {
    implementation(project(":app:shared"))
    implementation("com.tezov:tuucho.core-android:0.0.1-alpha22_2.2.21") // for kotlin 2.2.21   

    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("io.insert-koin:koin-core:4.1.1")
 }
```

Then you need to supply the context with the koin symbol `ApplicationModules.Name.APPLICATION_CONTEXT` 

```kotlin
object ApplicationModule {

    fun invoke(
        applicationContext: Context
    ) = module(ModuleGroupCore.Main) {
        single<Context>(SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT) {
            applicationContext
        }
    }

}
```

Then, in your `Activity`, set the Compose content to `AppScreen` and pass in the application context:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            AppScreen(
                listOf(
                    ConfigModule.invoke(applicationContext)),
                    ApplicationModule.invoke(applicationContext)
                )
            )
        }
    }
}
```

At runtime Tuucho will start Koin, load the configuration and display the server‑rendered UI.

## 5 iOS integration

On iOS you need to expose the `AppScreen` composable through a `ComposeUIViewController`, then wrap it in a SwiftUI view.  In your shared module create a simple function returning a `ComposeUIViewController`:

```kotlin
fun uiView() = ComposeUIViewController {
    AppScreen(
        applicationModuleDeclaration = listOf(
            ConfigModule.invoke(applicationContext)
        )
    )
}
```

Then in your iOS app, bridge the Compose controller into SwiftUI.  The sample uses `ComposeView` and `ContentView` to do this:

```swift
import AppSharedFramework

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainScreen_iosKt.uiView()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
    }
}
```

Present `ContentView` from your root view controller and Tuucho will render the server‑driven UI on iOS.

## 6 Backend

Tuucho renders its UI from a backend server.  For a quick test you can run the [tuucho‑backend](https://github.com/by-tezov/tuucho-backend) dev repository locally with a version matching your Tuucho client.

The engine is under active development.  At the time of writing the UI components are limited to basic primitives (fields, labels, linear layouts, buttons and spacers).  The ability to register custom views and provide richer UI will evolve in future releases.

## 7 Sample Application

* The `sample` folder is contains all explain above to try. [tuucho‑sample](https://github.com/by-tezov/tuucho)

!!! note
    The sample application includes a mock server. If you launch it in mock build type, you don't need a backend server.
