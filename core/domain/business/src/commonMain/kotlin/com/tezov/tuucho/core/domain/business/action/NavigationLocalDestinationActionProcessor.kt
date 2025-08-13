package com.tezov.tuucho.core.domain.business.action

import com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import kotlinx.serialization.json.JsonElement

class NavigationLocalDestinationActionProcessor(
    private val useCaseExecutor: UseCaseExecutor,
    private val navigateBack: NavigateBackUseCase
) : ActionProcessorProtocol {

    override val priority: Int
        get() = ActionProcessorProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return action.command == Action.Navigate.command && action.authority == Action.Navigate.LocalDestination.authority
    }

    override suspend fun process(
        route: NavigationRoute,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        useCaseExecutor.invokeSuspend(
            useCase = navigateBack,
            input = Unit
        )
    }

}