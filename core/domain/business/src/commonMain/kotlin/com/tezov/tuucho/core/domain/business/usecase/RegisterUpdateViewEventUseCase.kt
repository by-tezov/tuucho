package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.action.FormUpdateActionProcessor
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol

class RegisterUpdateViewEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
    private val formUpdateActionProcessor: FormUpdateActionProcessor,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
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
        coroutineScopes.event.async {
            shadowerMaterialRepository.events
                .filter { it.type == Shadower.Type.onDemandDefinition }
                .forever {
                    val views = navigationScreenStackRepository.getScreensOrNull(it.url)
                    coroutineScopes.renderer.await {
                        views?.forEach { view -> view.update(it.jsonObject) }
                    }
                }
        }
    }
}