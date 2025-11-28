package com.tezov.tuucho.core.barrel.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.NavigateAction
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import org.koin.core.Koin

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TuuchoBackHandler(
    koin: Koin
) {
    val useCaseExecutor = remember {
        koin.get<UseCaseExecutorProtocol>()
    }
    val actionHandler = remember {
        koin.get<ProcessActionUseCase>()
    }
    BackHandler(enabled = true) {
        // TODO add lock screen
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
