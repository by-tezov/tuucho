package com.tezov.tuucho.core.domain.business.interaction.action

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import kotlinx.serialization.json.JsonElement

class NavigationUrlActionProcessor(
    private val useCaseExecutor: UseCaseExecutor,
    private val navigateToUrl: NavigateToUrlUseCase,
) : ActionProcessorProtocol {

    override val priority: Int
        get() = ActionProcessorProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return action.command == Action.Navigate.command && action.authority == Action.Navigate.Url.authority
    }

    override suspend fun process(
        route: NavigationRoute,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        action.target?.let { url ->
            useCaseExecutor.invokeSuspend(
                useCase = navigateToUrl,
                input = NavigateToUrlUseCase.Input(
                    url = url
                )
            )
        }
    }

}