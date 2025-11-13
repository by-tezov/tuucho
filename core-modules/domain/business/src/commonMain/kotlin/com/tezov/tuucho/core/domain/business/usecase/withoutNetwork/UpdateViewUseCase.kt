package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Companion.execute
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase.Input
import kotlinx.serialization.json.JsonObject

class UpdateViewUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
    private val updateViewMiddlewares: List<UpdateViewMiddleware>
) : UseCaseProtocol.Sync<Input, Unit> {
    data class Input(
        val route: NavigationRoute.Url,
        val jsonObject: JsonObject,
    )

    override fun invoke(
        input: Input
    ) {
        coroutineScopes.useCase.async {
            updateViewMiddlewares.execute(
                context = UpdateViewMiddleware.Context(
                    input = input,
                ),
                terminal = terminalMiddleware()
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
