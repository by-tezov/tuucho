package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class NavigateFinishUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val navigationMiddlewares: List<NavigationMiddleware.Finish>,
) : UseCaseProtocol.Async<Unit, Unit>,
    TuuchoKoinComponent {
    override suspend fun invoke(
        input: Unit
    ) {
        coroutineScopes.useCase.await {
            middlewareExecutor.process(
                middlewares = navigationMiddlewares + terminalMiddleware(),
                context = Unit
            )
        }
    }

    private fun terminalMiddleware() = NavigationMiddleware.Finish { _, _ ->
        if (navigationMiddlewares.isEmpty()) {
            throw DomainException.Default(
                "You need to supply NavigationMiddleware.Finish to finish activity since the navigation stack is empty"
            )
        } else {
            throw DomainException.Default("you should finish activity and never call the last next of NavigationMiddleware.Finish")
        }
    }
}
