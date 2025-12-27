package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

private typealias ActionProjectionProtocols = ProjectionProtocols<() -> Unit>

interface ActionProjectionProtocol : ActionProjectionProtocols

class ActionProjection(
    private val route: NavigationRoute,
    private val projection: ActionProjectionProtocols
) : ActionProjectionProtocol,
    ActionProjectionProtocols by projection,
    TuuchoKoinComponent {
    init {
        attach(this)
    }

    override suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ) = (jsonElement as? JsonObject)
        ?.let { actionObject ->
            val koin = getKoin()
            val coroutineScopes = koin.get<CoroutineScopesProtocol>()
            val useCaseExecutor = koin.get<UseCaseExecutorProtocol>()
            val processAction = koin.get<ProcessActionUseCase>()
            val interactionLockResolver = koin.get<InteractionLockProtocol.Resolver>()
            val action: () -> Unit = {
                coroutineScopes.action.async(
                    throwOnFailure = true
                ) {
                    val screenLock = interactionLockResolver.tryAcquire(
                        requester = "$route::ActionProjection::${hashCode().toHexString()}",
                        lockable = InteractionLockable.Type(
                            value = listOf(InteractionLockType.Screen)
                        )
                    )
                    if (screenLock is InteractionLockable.Empty) {
                        return@async
                    }
                    useCaseExecutor.await(
                        useCase = processAction,
                        input = ProcessActionUseCase.Input.ActionObject(
                            route = route,
                            actionObject = actionObject,
                            lockable = screenLock.freeze()
                        )
                    )
                    interactionLockResolver.release(
                        requester = "$route::ActionProjection::${hashCode().toHexString()}",
                        lockable = screenLock
                    )
                }
            }
            action
        }
}

private class ContextualActionProjection(
    private val delegate: ActionProjectionProtocol
) : ActionProjectionProtocol by delegate,
    UpdatableProtocol {
    override val type = TypeSchema.Value.action

    override var id: String? = null
        private set

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (id == null) {
            jsonElement?.idValue?.let { id = it }
        }
        delegate.process(jsonElement)
    }
}

fun createActionProjection(
    key: String,
    route: NavigationRoute,
    mutable: Boolean,
    contextual: Boolean
): ActionProjectionProtocol {
    val projection: ActionProjectionProtocols = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val actionProjection = ActionProjection(route, projection)
    return when {
        contextual -> ContextualActionProjection(actionProjection)
        else -> actionProjection
    }
}
