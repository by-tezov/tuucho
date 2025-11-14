package com.tezov.tuucho.core.domain.business.interaction.action

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase

internal class NavigationLocalDestinationAction(
    private val useCaseExecutor: UseCaseExecutor,
    private val navigateBack: NavigateBackUseCase,
) : ActionMiddleware {
    override val priority: Int
        get() = ActionMiddleware.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
    ): Boolean = action.command == Action.Navigate.command && action.authority == Action.Navigate.LocalDestination.authority

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output?>
    ) = with(context.input) {
        when (action.target) {
            Action.Navigate.LocalDestination.Target.back -> useCaseExecutor.await(
                useCase = navigateBack,
                input = Unit
            )

            else -> throw DomainException.Default("Unknown target ${action.target}")
        }
        next.invoke(context)
    }
}
