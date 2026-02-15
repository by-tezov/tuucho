package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

typealias ActionTypeAlias = (jsonElement: JsonElement?) -> Unit

private typealias ActionProjectionTypeAlias = ProjectionProtocols<ActionTypeAlias>

interface ActionProjectionProtocol :
    IdProcessorProtocol,
    ActionProjectionTypeAlias,
    ResolveStatusProcessorProtocol

private class ActionProjection(
    private val route: NavigationRoute,
    private val idProcessor: IdProcessorProtocol,
    private val projection: ActionProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : ActionProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    ActionProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status,
    TuuchoKoinComponent {
    init {
        attach(this as ExtractorProjectionProtocol<ActionTypeAlias>)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        idProcessor.process(jsonElement)
        projection.process(jsonElement)
        status.update(jsonElement)
    }

    override suspend fun extract(
        jsonElement: JsonElement?
    ) = (jsonElement as? JsonObject)
        ?.let { actionObject ->
            val koin = getKoin()
            val coroutineScopes = koin.get<CoroutineScopesProtocol>()
            val useCaseExecutor = koin.get<UseCaseExecutorProtocol>()
            val processAction = koin.get<ProcessActionUseCase>()
            val interactionLockResolver = koin.get<InteractionLockProtocol.Resolver>()
            val action: ActionTypeAlias = { jsonElement ->
                coroutineScopes.default.async {
                    val screenLock = interactionLockResolver.tryAcquire(
                        requester = "$route::ActionProjection::${hashCode().toHexString()}",
                        lockable = InteractionLockable.Types(
                            values = listOf(InteractionLockType.Screen)
                        )
                    )
                    if (screenLock is InteractionLockable.Empty) {
                        return@async
                    }
                    useCaseExecutor.await(
                        useCase = processAction,
                        input = ProcessActionUseCase.Input.create(
                            route = route,
                            modelObject = actionObject,
                            lockable = screenLock.freeze(),
                            jsonElement = jsonElement
                        )
                    )
                    interactionLockResolver.release(
                        requester = "$route::ActionProjection::${hashCode().toHexString()}",
                        lockable = screenLock
                    )
                }.start()
            }
            action
        }
}

private class MutableActionProjection(
    delegate: ActionProjectionProtocol,
    storage: StorageProjectionProtocol<ActionTypeAlias>
) : ActionProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

val ActionProjectionProtocol.mutable
    get(): ActionProjectionProtocol = MutableActionProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

fun createActionProjection(
    key: String,
    route: NavigationRoute,
): ActionProjectionProtocol = ActionProjection(
    route = route,
    idProcessor = IdProcessor(),
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
)

fun TypeProjectorProtocols.action(
    key: String,
    route: NavigationRoute
): ActionProjectionProtocol = createActionProjection(key, route)
