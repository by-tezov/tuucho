# UseCases

Tuucho allows you to execute various use cases to interact with the engine. These use cases must be executed using the `UseCaseExecutorProtocol` instance injected with Koin.

The executor exposes two execution modes:

```kotlin
interface UseCaseExecutorProtocol {
    fun <INPUT : Any, OUTPUT : Any> async(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
        onException: ((DomainException) -> Unit)? = null,
        onResult: OUTPUT?.() -> Unit = {},
    )

    suspend fun <INPUT : Any, OUTPUT : Any> await(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
    ): OUTPUT?
}
```

- **async** → fire & forget (or use a deferred result at another time)
- **await** → suspends execution until the result is produced

---

Example: Retrieve a Validator From a Prototype

```kotlin
useCaseExecutor
    .await(
        useCase = fieldValidatorFactory,
        input = FormValidatorFactoryUseCase.Input(
            prototypeObject = validatorPrototype
        ),
    )
    ?.validator as? FormValidatorProtocol<String>
```

---

Example: Trigger an Action

```kotlin
useCaseExecutor.async(
    useCase = actionHandler,
    input = ProcessActionUseCase.Input.ActionObject(
        route = route,
        actionObject = actionObject,
        lockable = null
    )
)
```

---

# ProcessActionUseCase

This is the most important use case in Tuucho. It executes **actions**, which are the core mechanism behind:

- navigating
- storing data
- sending data
- contextual UI updates
- and any custom action you add

```kotlin
class ProcessActionUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val actionExecutor: ActionExecutorProtocol,
) : UseCaseProtocol.Async<Input, Output> {

    data class Input(
        val route: NavigationRoute?,
        val models: List<ActionModel>,
        val modelObjectOriginal: JsonElement? = null,
        val lockable: InteractionLockable? = null,
        val jsonElement: JsonElement? = null
    ) {
        companion object {
            fun create(
                route: NavigationRoute?,
                modelObject: JsonObject,
                lockable: InteractionLockable? = null,
                jsonElement: JsonElement? = null,
            ) = buildList { ... }

            fun create(
                route: NavigationRoute?,
                models: List<ActionModel>,
                lockable: InteractionLockable? = null,
                jsonElement: JsonElement? = null,
            ) = Input(
                route = route,
                models = models,
                lockable = lockable,
                jsonElement = jsonElement,
            )

            fun create(
                route: NavigationRoute?,
                model: ActionModel,
                lockable: InteractionLockable? = null,
                jsonElement: JsonElement? = null,
            ) = Input(
                route = route,
                models = listOf(model),
                lockable = lockable,
                jsonElement = jsonElement,
            )
        }
    }

    override suspend fun invoke(
        input: Input
    ) {
        actionExecutor.process(input = input)
    }
}
```

---

## Example: Navigate Using an Action

```kotlin
useCaseExecutor.async(
    useCase = actionHandler,
    input = ProcessActionUseCase.Input.create(
        route = null,
        model = ActionModelDomain.from(
            command = NavigateAction.command,
            authority = NavigateAction.Url.authority,
            target = "target url",
        )
    ),
)
```

Existing actions are available here: [object-definition/action/](../object-definition/action.md)

You can also implement your **own actions** as needed. Documentation not done yet.

---

## RefreshMaterialCacheUseCase

This use case refreshes the **material JSON cache**. Typically executed during startup.

```kotlin
class RefreshMaterialCacheUseCase(
    private val refreshMaterialCacheRepository: MaterialRepositoryProtocol.RefreshCache,
) : UseCaseProtocol.Async<RefreshMaterialCacheUseCase.Input, Unit> {
    
    data class Input(
        val url: String,
    )

    override suspend fun invoke(
        input: Input
    ) {
        refreshMaterialCacheRepository.process(input.url)
    }
}
```

See:  

- [config.md](config.md)
- Check [tuucho‑sample](https://github.com/by-tezov/tuucho) file in share module commonMain (`BeforeNavigateToUrlMiddleware`)

---

# Key-Value Store UseCases

Tuucho provides several use cases to interact with the datastore.

⚠️ **Recommended method to save values:**  
- 
- Use the action processor with: `store://key-value/save?{key}={value}&{key}={value}`
- check: [object-definition/action/](../object-definition/action.md)

The following use cases provide low-level direct access.

---

## GetValueOrNullFromStoreUseCase

```kotlin
class GetValueOrNullFromStoreUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Output> {
    
    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key,
    )

    data class Output(
        val value: KeyValueStoreRepositoryProtocol.Value?,
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        coroutineScopes.io.await {
            Output(
                value = keyValueRepository.getOrNull(key)
            )
        }
    }
}
```

---

## RemoveKeyValueFromStoreUseCase

```kotlin
class RemoveKeyValueFromStoreUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Unit> {
    
    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key
    )

    override suspend fun invoke(
        input: Input
    ) = keyValueRepository.save(input.key, null)
}
```

---

## HasKeyInStoreUseCase

```kotlin
class HasKeyInStoreUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Output> {
    
    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key
    )

    data class Output(
        val result: Boolean,
    )

    override suspend fun invoke(
        input: Input
    ) = Output(
        result = keyValueRepository.hasKey(input.key)
    )
}
```

---

## SaveKeyValueToStoreUseCase

```kotlin
class SaveKeyValueToStoreUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Unit> {
    
    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key,
        val value: KeyValueStoreRepositoryProtocol.Value?,
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        keyValueRepository.save(key, value)
    }
}
```

---

# Advanced UseCases

Tuucho exposes additional use cases for advanced workflows, including:

- Creating **custom view component features**  
- Retrieving the **current screen**  
- Registering listeners to **navigation transition events**  
- Retrieving validators from prototypes for form validation  
- Observing or extending internal rendering/update pipelines  

These will be fully documented in future updates.
