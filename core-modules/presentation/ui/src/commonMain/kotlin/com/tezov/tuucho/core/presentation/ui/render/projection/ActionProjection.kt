package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.runtime.MutableState
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object ActionProjection : TuuchoKoinComponent {

    private fun getValue(
        jsonElement: JsonElement?,
        route: NavigationRoute,
    ): (() -> Unit)? = (jsonElement as? JsonObject)
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

    class Static(
        key: String,
        private val route: NavigationRoute
    ) : Projection.AbstractStatic<() -> Unit>(key) {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = getValue(jsonElement, route)
    }

    class Mutable(
        key: String,
        private val route: NavigationRoute
    ) : Projection.AbstractMutable<() -> Unit>(key),
        MutableState<(() -> Unit)?> {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = getValue(jsonElement, route)
    }
}
