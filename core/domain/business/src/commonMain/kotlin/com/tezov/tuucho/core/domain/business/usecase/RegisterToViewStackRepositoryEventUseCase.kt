package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.RegisterToViewStackRepositoryEventUseCase.Input

class RegisterToViewStackRepositoryEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val viewContextStackRepository: ViewContextStackRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Unit> {

    data class Input(
        val onEvent: (url: String) -> Unit,
    )

    override suspend fun invoke(input: Input): Unit = with(input) {
        coroutineScopes.onEvent {
            viewContextStackRepository.events
                .forever { event ->
                    (event as? NavigationRoute.Url)
                        ?.let {
                            onEvent(it.value)
                        }
                }
        }
    }

}