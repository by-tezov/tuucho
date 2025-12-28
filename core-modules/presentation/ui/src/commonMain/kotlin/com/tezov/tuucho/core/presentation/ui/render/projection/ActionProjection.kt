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
import com.tezov.tuucho.core.presentation.ui.render.misc.ReadyStatus
import com.tezov.tuucho.core.presentation.ui.render.misc.Updatable
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

private typealias ActionProjectionTypeAlias = ProjectionProtocols<() -> Unit>

interface ActionProjectionProtocol : ActionProjectionTypeAlias

class ActionProjection(
    private val route: NavigationRoute,
    private val projection: ActionProjectionTypeAlias
) : ActionProjectionProtocol,
    ActionProjectionTypeAlias by projection,
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
    private val delegate: ActionProjectionProtocol,
    private val updatable: UpdatableProtocol,
    private val status: ReadyStatusProtocol
) : ActionProjectionProtocol by delegate,
    UpdatableProtocol by updatable,
    ReadyStatusProtocol by status {

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        delegate.process(jsonElement)
        updatable.process(jsonElement)
        status.update(jsonElement)
    }
}

fun createActionProjection(
    key: String,
    route: NavigationRoute,
    mutable: Boolean,
    contextual: Boolean
): ActionProjectionProtocol {
    val projection: ActionProjectionTypeAlias = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val actionProjection = ActionProjection(route, projection)
    return when {
        contextual -> ContextualActionProjection(
            delegate = actionProjection,
            updatable = Updatable(TypeSchema.Value.action),
            status = ReadyStatus()
        )

        else -> actionProjection
    }
}
