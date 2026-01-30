package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware.Context
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.model.action.NavigateActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase
import kotlinx.coroutines.flow.FlowCollector

internal class NavigationUrlActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val navigateToUrl: NavigateToUrlUseCase,
) : ActionMiddleware {
    override val priority: Int
        get() = ActionMiddleware.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = action.command == NavigateActionDefinition.Url.command &&
        action.authority == NavigateActionDefinition.Url.authority

    override suspend fun FlowCollector<Unit>.process(
        context: Context,
        next: MiddlewareProtocol.Next<Context, Unit>?
    ) {
        context.actionModel.target?.let { url ->
            useCaseExecutor.await(
                useCase = navigateToUrl,
                input = NavigateToUrlUseCase.Input(
                    url = url,
                )
            )
        }
        next.invoke(context)
    }
}
