package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.RegisterToScreenStackRepositoryEventUseCase.Input

class RegisterToScreenStackRepositoryEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
) : UseCaseProtocol.Async<Input, Unit> {

    data class Input(
        val onEvent: (screenIdentifier: ScreenProtocol.IdentifierProtocol) -> Unit,
    )

    override suspend fun invoke(input: Input): Unit = with(input) {
        coroutineScopes.onEvent {
            navigationScreenStackRepository.events
                .forever { event ->
                    onEvent(event)
                }
        }
    }

}