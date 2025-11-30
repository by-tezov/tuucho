package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase.Input
import kotlinx.serialization.json.JsonObject

class UpdateViewUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val updateViewMiddlewares: List<UpdateViewMiddleware>
) : UseCaseProtocol.Async<Input, Unit> {
    data class Input(
        val route: NavigationRoute,
        val jsonObject: JsonObject,
    )

    override suspend fun invoke(
        input: Input
    ) {
        coroutineScopes.useCase.await {
            middlewareExecutor.process(
                middlewares = updateViewMiddlewares + terminalMiddleware(),
                context = UpdateViewMiddleware.Context(
                    input = input,
                )
            )
        }
    }

    private fun terminalMiddleware(): UpdateViewMiddleware = UpdateViewMiddleware { context, _ ->
        with(context.input) {
            val view = navigationScreenStackRepository.getScreenOrNull(route)
            coroutineScopes.renderer.await {
                view?.update(jsonObject)
            }
        }
    }
}
