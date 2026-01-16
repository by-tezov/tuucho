# Action

Tuucho provides several built-in actions. You can see the full list here:

[object-definition/action/](../object-definition/action.md)

You can also add middleware around built-in actions by implementing the `ActionMiddleware` interface.

---

## Example: Logger Middleware for Actions

```kotlin
class LoggerAction(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : ActionMiddleware {

    override val priority: Int = ActionMiddleware.Priority.LOW

    override fun accept(
        route: NavigationRoute?,
        action: ActionModelDomain,
    ) = true

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>?
    ) = with(context.input) {
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.debug("ACTION") { "from ${route?.value}: $action" }
        next?.invoke(context)
    }
}
```

You can completely intercept an action or alter it before continuing the chain.

Register it in Koin:

```kotlin
module(ModuleContextDomain.Middleware) {
    factoryOf(::LoggerAction) bind ActionMiddleware::class
}
```

Action middleware **are not ordered**. They use a **priority system**. You can override `priority` to influence execution order.

---

## Calling Built-In Actions

### Example Forward navigation

```kotlin
useCaseExecutor.async(
    useCase = actionHandler,
    input = ProcessActionUseCase.Input.JsonElement(
        route = null,
        action = ActionModelDomain.from(
            command = NavigateAction.command,
            authority = NavigateAction.Url.authority,
            target = "target url",
        )
    ),
)
```

### Example Back navigation

```kotlin
useCaseExecutor.async(
    useCase = actionHandler,
    input = ProcessActionUseCase.Input.JsonElement(
        route = null,
        action = ActionModelDomain.from(
            command = NavigateAction.command,
            authority = NavigateAction.LocalDestination.authority,
            target = NavigationRoute.Back,
        )
    ),
)
```

Actions are processed by the `ActionExecutor`. All actions you register are inserted as middleware.

---

# Creating Your Own Action

You can declare new actions for your application. Here is an example of a **custom crash action**:

```kotlin
class CrashApplicationActionMiddleware() : ActionMiddleware {

    override val priority: Int
        get() = ActionMiddleware.Priority.HIGH

    override fun accept(
        route: NavigationRoute?,
        action: ActionModelDomain,
    ): Boolean = action.command == "crash-application" && action.authority == "polite"

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>?
    ) = with(context.input) {

        val message = context.input.action.query?.jsonObject["message"].stringOrNull
        throw RuntimeException("crash action with message $message")

        // can't be reach because we crash the application, but when you create action,
        // you must always call it to continue the chain
        // and allow adding extension to tis action
        next?.invoke(context)
    }
}
```

Register the custom action in Koin:

```kotlin
module(ModuleContextDomain.Middleware) {
    factoryOf(::CrashApplicationActionMiddleware) bind ActionMiddleware::class
}
```

---
