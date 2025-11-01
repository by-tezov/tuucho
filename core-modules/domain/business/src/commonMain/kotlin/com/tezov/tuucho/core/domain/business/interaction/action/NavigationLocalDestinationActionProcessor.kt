package com.tezov.tuucho.core.domain.business.interaction.action

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase
import kotlinx.serialization.json.JsonElement

internal class NavigationLocalDestinationActionProcessor(
    private val useCaseExecutor: UseCaseExecutor,
    private val navigateBack: NavigateBackUseCase,
) : ActionProcessorProtocol {

    override val priority: Int
        get() = ActionProcessorProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return action.command == Action.Navigate.command && action.authority == Action.Navigate.LocalDestination.authority
    }

    override suspend fun process(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        when (action.target) {
            Action.Navigate.LocalDestination.Target.back -> useCaseExecutor.invokeSuspend(
                useCase = navigateBack,
                input = Unit
            )

            else -> throw DomainException.Default("Unknown target ${action.target}")
        }
    }

}