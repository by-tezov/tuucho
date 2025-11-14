package com.tezov.tuucho.core.domain.business.interaction.action

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase

internal class NavigationUrlAction(
    private val useCaseExecutor: UseCaseExecutor,
    private val navigateToUrl: NavigateToUrlUseCase,
) : ActionMiddleware {
    override val priority: Int
        get() = ActionMiddleware.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
    ): Boolean = action.command == Action.Navigate.command && action.authority == Action.Navigate.Url.authority

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output?>
    ) = with(context.input) {
        action.target?.let { url ->
            useCaseExecutor.await(
                useCase = navigateToUrl,
                input = NavigateToUrlUseCase.Input(
                    url = url
                )
            )
        }
        next.invoke(context)
    }
}
