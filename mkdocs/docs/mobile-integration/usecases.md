# UseCases

Tuucho allows you to execute various use cases to interact with the engine. These use cases must be executed using the `UseCaseExecutorProtocol` instance injected with Koin.

The executor exposes two execution modes:

```kotlin
interface UseCaseExecutorProtocol {
    fun async(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
        onException: ((DomainException) -> Unit)? = null,
        onResult: OUTPUT?.() -> Unit = {},
    )

    suspend fun await(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
    ): OUTPUT?
}
```

- **async** → fire & forget
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
    
    sealed class Input {
        abstract val route: NavigationRoute.Url?
        abstract val lockable: InteractionLockable?

        data class JsonElement(
            override val route: NavigationRoute.Url?,
            val action: ActionModelDomain,
            override val lockable: InteractionLockable? = null,
            val jsonElement: kotlinx.serialization.json.JsonElement? = null,
        ) : Input()

        data class ActionObject(
            override val route: NavigationRoute.Url?,
            val actionObject: JsonObject,
            override val lockable: InteractionLockable? = null,
        ) : Input()
    }

    sealed class Output {
        class Element(
            val type: KClass<out Any>,
            val rawValue: Any,
        ) : Output() {
            @Suppress("UNCHECKED_CAST")
            inline fun <reified T> value(): T = rawValue as T

            @Suppress("UNCHECKED_CAST")
            inline fun <reified T> valueOrNull(): T? = rawValue as? T
        }

        class ElementArray(
            val values: List<Output>,
        ) : Output()
    }

    override suspend fun invoke(
        input: Input
    ) = coroutineScopes.useCase.await {
        actionExecutor.process(input = input)
    }
}
```

---

## Example: Navigate Using an Action

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
        with(input) {
            refreshMaterialCacheRepository.process(url)
        }
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
    ) = with(input) {
        coroutineScopes.io.await {
            keyValueRepository.save(key, null)
        }
    }
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
    ) = with(input) {
        coroutineScopes.io.await {
            Output(
                result = keyValueRepository.hasKey(key)
            )
        }
    }
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
        coroutineScopes.io.await {
            keyValueRepository.save(key, value)
        }
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
