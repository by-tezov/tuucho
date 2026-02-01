package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.model.action.NavigateActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol.Context
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigateFinishUseCase

internal class NavigationLocalDestinationActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val navigateBack: NavigateBackUseCase,
    private val navigateFinish: NavigateFinishUseCase,
) : ActionMiddlewareProtocol {
    override val priority: Int
        get() = ActionMiddlewareProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = action.command == NavigateActionDefinition.LocalDestination.command &&
        action.authority == NavigateActionDefinition.LocalDestination.authority

    override suspend fun process(
        context: Context,
        next: MiddlewareProtocol.Next<Context>?
    ) {
        when (context.actionModel.target) {
            NavigateActionDefinition.LocalDestination.Target.back -> {
                useCaseExecutor.await(
                    useCase = navigateBack,
                    input = Unit
                )
            }

            NavigateActionDefinition.LocalDestination.Target.finish -> {
                useCaseExecutor.await(
                    useCase = navigateFinish,
                    input = Unit
                )
            }

            else -> {
                throw DomainException.Default("Unknown target ${context.actionModel.target}")
            }
        }
        next?.invoke(context)
    }
}
