package com.tezov.tuucho.core.barrel.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.NavigateAction
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.tool.async.DeferredExtension.throwOnFailure
import org.koin.core.Koin

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TuuchoBackHandler(
    koin: Koin
) {
    val coroutineScopes = remember {
        koin.get<CoroutineScopesProtocol>()
    }
    val useCaseExecutor = remember {
        koin.get<UseCaseExecutorProtocol>()
    }
    val interactionLockResolver = remember {
        koin.get<InteractionLockProtocol.Resolver>()
    }
    val actionHandler = remember {
        koin.get<ProcessActionUseCase>()
    }
    BackHandler(enabled = true) {
        coroutineScopes.action.async {
            val screenLock = interactionLockResolver.acquire(
                requester = "BackHandler",
                lockable = InteractionLockable.Type(
                    value = listOf(InteractionLockType.Screen)
                )
            )
            useCaseExecutor.await(
                useCase = actionHandler,
                input = ProcessActionUseCase.Input.JsonElement(
                    route = NavigationRoute.Current,
                    action = ActionModelDomain.from(
                        command = NavigateAction.command,
                        authority = NavigateAction.LocalDestination.authority,
                        target = NavigateAction.LocalDestination.Target.back,
                    ),
                    lockable = screenLock.freeze()
                )
            )
            interactionLockResolver.release(
                requester = "BackHandler",
                lockable = screenLock
            )
        }.throwOnFailure()
    }
}
