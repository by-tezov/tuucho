package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.business.model.schema.material.Shadower
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol

class RegisterUpdateViewEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
    private val formUpdateActionHandler: FormUpdateActionHandler,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
) : UseCaseProtocol.Sync<Unit, Unit> {

    override fun invoke(input: Unit) {
        coroutineScopes.event.launch {
            formUpdateActionHandler.events
                .forever {
                    val view = navigationScreenStackRepository.getView(it.source)
                    coroutineScopes.renderer.on {
                        view?.update(it.jsonObject)
                    }

                }
        }
        coroutineScopes.event.launch {
            shadowerMaterialRepository.events
                .filter { it.type == Shadower.Type.onDemandDefinition }
                .forever {
                    val views = navigationScreenStackRepository.getViews(it.url)
                    coroutineScopes.renderer.on {
                        views?.forEach { view -> view.update(it.jsonObject) }
                    }
                }
        }
    }
}