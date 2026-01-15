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
        onBackCompleted(
            coroutineScopes = coroutineScopes,
            useCaseExecutor = useCaseExecutor,
            interactionLockResolver = interactionLockResolver,
            actionHandler = actionHandler
        )
    }

//    NavigationBackHandler(
//        state = rememberNavigationEventState(currentInfo = NavigationEventInfo.None),
//        isBackEnabled = true,
//        onBackCompleted = {
//            onBackCompleted(
//                coroutineScopes = coroutineScopes,
//                useCaseExecutor = useCaseExecutor,
//                interactionLockResolver = interactionLockResolver,
//                actionHandler = actionHandler
//            )
//        }
//    )

}

private fun onBackCompleted(
    coroutineScopes: CoroutineScopesProtocol,
    useCaseExecutor: UseCaseExecutorProtocol,
    interactionLockResolver: InteractionLockProtocol.Resolver,
    actionHandler: ProcessActionUseCase,
) {
    coroutineScopes.action.async(
        throwOnFailure = true,
    ) {
        val screenLock = interactionLockResolver.acquire(
            requester = "BackHandler",
            lockable = InteractionLockable.Type(
                value = listOf(InteractionLockType.Screen)
            )
        )
        useCaseExecutor.await(
            useCase = actionHandler,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Current,
                action = ActionModelDomain.from(
                    command = NavigateAction.LocalDestination.command,
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
    }
}
