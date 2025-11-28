package com.tezov.tuucho.core.barrel.interaction.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.tezov.tuucho.core.barrel.localTuuchoKoin
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.NavigateAction
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TuuchoBackHandler() {
    val koin = localTuuchoKoin.current
    androidx.compose.ui.backhandler.BackHandler(
        enabled = true
    ) {
        // TODO add lock screen
        val useCaseExecutor = koin.get<UseCaseExecutorProtocol>()
        val actionHandler = koin.get<ProcessActionUseCase>()
        useCaseExecutor.async(
            useCase = actionHandler,
            input = ProcessActionUseCase.Input.JsonElement(
                route = NavigationRoute.Current,
                action = ActionModelDomain.from(
                    command = NavigateAction.command,
                    authority = NavigateAction.LocalDestination.authority,
                    target = NavigateAction.LocalDestination.Target.back,
                )
            )
        )
    }
}
