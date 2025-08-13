package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.RegisterToScreenTransitionEventUseCase.Input

class RegisterToScreenTransitionEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationAnimatorStackRepository: NavigationRepositoryProtocol.StackAnimator,
) : UseCaseProtocol.Sync<Input, Unit> {

    data class Input(
        val onEvent: suspend (animate: Boolean) -> Unit,
    )

    override fun invoke(input: Input) {
        coroutineScopes.event.async {
            navigationAnimatorStackRepository.animate
                .forever { input.onEvent.invoke(it) }
        }
    }
}