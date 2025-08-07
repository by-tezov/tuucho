package com.tezov.tuucho.core.domain.business.actionHandler

import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import kotlinx.serialization.json.JsonElement

class NavigationLocalDestinationActionHandler(
    private val useCaseExecutor: UseCaseExecutor,
    private val navigateBack: NavigateBackUseCase
) : ActionHandlerProtocol {

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(
        source: SourceIdentifierProtocol,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return action.command == Action.Navigate.command && action.authority == Action.Navigate.LocalDestination.authority
    }

    override suspend fun process(
        source: SourceIdentifierProtocol,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        useCaseExecutor.invokeSuspend(
            useCase = navigateBack,
            input = Unit
        )
    }

}