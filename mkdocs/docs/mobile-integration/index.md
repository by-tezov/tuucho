---
comments: true
---

# Tuucho Documentation

Tuucho provides several extension points to customize application behavior:

- **Monitors**
- **Interceptors**
- **Middleware**
- **ActionMiddleware**

It also includes many **built-in features**, while still allowing you to add your own logic at every critical layer of the engine.

To get started, refer to the quick start guide: [quick-start.md](quick-start.md)

The [tuucho‑sample](https://github.com/by-tezov/tuucho) is also extremely valuable: it contains ready-to-use code demonstrating navigation, actions, middleware, monitors, configuration, use cases, and more.

---

# Basic Integration

Using Tuucho inside your project is simple, once all modules and configuration are properly set up.

## Android Example

```kotlin
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { 
            AppScreen(
                listOf(
                    ApplicationModule.invoke(applicationContext)
                )
            ) 
        }
    }

}
```

## iOS Example (SwiftUI + Compose Multiplatform)

```swift
struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        MainScreen_iosKt.uiView()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
        //.ignoresSafeArea(.keyboard)
    }
}
```

Once your Koin setup, modules, configuration, and platform wiring are ready, launching Tuucho is effortless xD.

---

- [Dependencies Injection](di.md)
- [Configuration](config.md)
- [Interceptors](interceptor.md)
- [Monitors](monitor.md)
- [Middleware](middleware.md)
- [Action Middlewares](action.md)
- [UseCases](usecases.md)
---

# Note

Tuucho is still actively in development. The next focus will be on the **presentation layer**:

- providing solid default UI bricks ready to use
- allowing you to easily add your own custom UI components
- strengthening the rendering pipeline
- improving the server-driven view architecture

A lot of work has already been done, and if you've been following the project since the beginning, you can now start to see what Tuucho is becoming.

More features, stability improvements, and documentation updates are coming soon.

Tuucho is evolving fast — and you're early enough to watch it take shape.
