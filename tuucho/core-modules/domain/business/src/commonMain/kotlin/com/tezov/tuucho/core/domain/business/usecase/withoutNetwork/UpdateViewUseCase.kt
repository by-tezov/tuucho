package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol.Companion.asFlow
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol.Companion.asHotFlow
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
class UpdateViewUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val updateViewMiddlewares: List<UpdateViewMiddleware>
) : UseCaseProtocol.Async<Input, Unit> {
    data class Input(
        val route: NavigationRoute,
        val jsonObjects: List<JsonObject>,
    )

    override suspend fun invoke(
        input: Input
    ) {
        middlewareExecutor.asFlow(
            middlewares = updateViewMiddlewares + terminalMiddleware(),
            context = UpdateViewMiddleware.Context(
                input = input,
            )
        ).asHotFlow(coroutineScopes.useCase)
    }

    private fun terminalMiddleware(): UpdateViewMiddleware = UpdateViewMiddleware { context, _ ->
        with(context.input) {
            val screen = navigationScreenStackRepository.getScreenOrNull(route)
            screen?.update(jsonObjects)
        }
    }
}
