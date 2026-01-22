package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.model.action.NavigateActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase

internal class NavigationLocalDestinationActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val navigateBack: NavigateBackUseCase,
) : ActionMiddleware {
    override val priority: Int
        get() = ActionMiddleware.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = action.command == NavigateActionDefinition.LocalDestination.command &&
        action.authority == NavigateActionDefinition.LocalDestination.authority

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output.ElementArray>?
    ) = with(context.input) {
        when (actionModel.target) {
            NavigateActionDefinition.LocalDestination.Target.back -> {
                useCaseExecutor.await(
                    useCase = navigateBack,
                    input = Unit
                )
            }

            else -> {
                throw DomainException.Default("Unknown target ${actionModel.target}")
            }
        }
        next?.invoke(context)
    }
}
