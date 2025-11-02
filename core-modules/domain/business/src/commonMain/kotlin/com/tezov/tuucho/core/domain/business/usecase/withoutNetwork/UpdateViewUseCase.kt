package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase.Input
import kotlinx.serialization.json.JsonObject

class UpdateViewUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
) : UseCaseProtocol.Async<Input, Unit> {
    data class Input(
        val route: NavigationRoute.Url,
        val jsonObject: JsonObject,
    )

    override suspend fun invoke(
        input: Input
    ) {
        with(input) {
            val view = navigationScreenStackRepository.getScreenOrNull(route)
            coroutineScopes.renderer.await {
                view?.update(jsonObject)
            }
        }
    }
}
