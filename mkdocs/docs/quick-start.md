---
comments: true
---

# Quick Start

This guide shows how to quickly integrate **Tuucho** into a Kotlin Multiplatform project (iOS and Android). Tuucho is a server‑driven rendering engine published on Maven Central.

## 1 Add dependencies to the shared module

In your **shared module** (`commonMain`) you need the Tuucho core library plus its supporting libraries:

```kotlin
 commonMain.dependencies {
    implementation("com.tezov:tuucho.core:0.0.1-alpha13_2.2.10") // for kotlin 2.2.10     
    implementation("io.insert-koin:koin-core:4.1.1")     
    implementation("io.insert-koin:koin-compose:4.1.1")

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
    applicationModuleDeclaration: ModuleDeclaration,
) = StartKoinModules(
    configurationModuleDeclaration + applicationModuleDeclaration
) {
    val tuuchoEngine = rememberTuuchoEngine()
    LaunchedEffect(Unit) {
        tuuchoEngine.load(url = "config")
        tuuchoEngine.start(url = "page-home")
    }
    tuuchoEngine.display()
}
```

The sample repository uses this pattern – the `AppScreen` composable calls `rememberTuuchoEngine`, loads a configuration to preload / cache or update content. Take a look to [Config details](config/index.md)

## 3 Supply configuration via Koin in the shared module

Tuucho needs the database file name and base url of your backend.  Define a Koin module that provides a `SystemCoreDataModules.Config` instance.  In the shared module you can implement this interface with your own values:

```kotlin
val configurationModuleDeclaration: ModuleDeclaration = {
        factory<SystemCoreDataModules.Config> {
            object : SystemCoreDataModules.Config {
            override val localDatabaseFile: String = "database.db"
            override val serverUrl: String = "http://localhost:3000/"
        }
    }
}
```

The core module defines the `Config` interface with two properties (`localDatabaseFile` and `serverUrl`).  

The sample application uses `BuildKonfig` to generate these values per platform.

## 4 Android integration

On Android you have to provide the application `Context` so that Tuucho can access platform services.  Define an `ApplicationModuleDeclaration` that injects the context using Koin:

First you need the to add the dependencies

```kotlin
 dependencies {
    implementation(project(":app:shared"))
    implementation("com.tezov:tuucho.core-android:0.0.1-alpha13_2.2.10") // for kotlin 2.2.10   

    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("io.insert-koin:koin-core:4.1.1")
 }
```

Then you need to supply the context with the koin symbol `ApplicationModules.Name.APPLICATION_CONTEXT` 

```kotlin
object ApplicationModuleDeclaration {
    operator fun invoke(applicationContext: Context): ModuleDeclaration = {
        single<Context>(ApplicationModules.Name.APPLICATION_CONTEXT) {
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
            AppScreen(ApplicationModuleDeclaration.invoke(applicationContext))
        }
    }
}
```

At runtime Tuucho will start Koin, load the configuration and display the server‑rendered UI.

## 5 iOS integration

On iOS you need to expose the `AppScreen` composable through a `ComposeUIViewController`, then wrap it in a SwiftUI view.  In your shared module create a simple function returning a `ComposeUIViewController`:

```kotlin
fun uiView() = ComposeUIViewController {
    AppScreen(applicationModuleDeclaration = {})
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

Tuucho renders its UI from a backend server.  For a quick test you can run the [tuucho‑backend](https://github.com/by-tezov/tuucho-backend) repository with a version matching your Tuucho client.

The engine is under active development.  At the time of writing the UI components are limited to basic primitives (fields, labels, linear layouts, buttons and spacers).  The ability to register custom views and provide richer UI will evolve in future releases.

## 7 Sample Application

* The `sample` folder is contains all explain above to try. [tuucho‑sample](https://github.com/by-tezov/tuucho)
