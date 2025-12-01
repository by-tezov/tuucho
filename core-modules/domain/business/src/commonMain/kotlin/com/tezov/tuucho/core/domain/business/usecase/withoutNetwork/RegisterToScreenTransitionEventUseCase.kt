package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackTransition.Event
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RegisterToScreenTransitionEventUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.async.DeferredExtension.throwOnFailure

@OpenForTest
class RegisterToScreenTransitionEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationAnimatorStackRepository: NavigationRepositoryProtocol.StackTransition,
) : UseCaseProtocol.Sync<Input, Unit> {
    data class Input(
        val onEvent: suspend (animate: Event) -> Unit,
    )

    override fun invoke(
        input: Input
    ) {
        coroutineScopes.event
            .async {
                navigationAnimatorStackRepository.events
                    .filter { it != Event.TransitionComplete }
                    .forever { input.onEvent.invoke(it) }
            }.throwOnFailure()
    }
}
