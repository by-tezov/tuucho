package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.action.FormUpdateActionProcessor
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol

class RegisterUpdateViewEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
    private val formUpdateActionProcessor: FormUpdateActionProcessor,
) : UseCaseProtocol.Sync<Unit, Unit> {

    override fun invoke(input: Unit) {
        coroutineScopes.event.async {
            formUpdateActionProcessor.events
                .forever {
                    val view = navigationScreenStackRepository.getScreenOrNull(it.route)
                    coroutineScopes.renderer.await {
                        view?.update(it.jsonObject)
                    }

                }
        }
    }
}