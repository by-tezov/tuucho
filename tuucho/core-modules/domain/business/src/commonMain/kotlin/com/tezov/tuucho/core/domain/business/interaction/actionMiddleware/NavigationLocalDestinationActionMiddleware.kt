package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.NavigateAction
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
        action: ActionModelDomain,
    ) = action.command == NavigateAction.LocalDestination.command && action.authority == NavigateAction.LocalDestination.authority

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>?
    ) = with(context.input) {
        when (action.target) {
            NavigateAction.LocalDestination.Target.back -> {
                useCaseExecutor.await(
                    useCase = navigateBack,
                    input = Unit
                )
            }

            else -> {
                throw DomainException.Default("Unknown target ${action.target}")
            }
        }
        next?.invoke(context)
    }
}
