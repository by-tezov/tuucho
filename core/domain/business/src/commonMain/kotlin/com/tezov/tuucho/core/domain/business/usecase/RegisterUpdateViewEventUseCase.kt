package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.business.model.schema.material.Shadower
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ShadowerMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.state.UpdateViewUseCase

class RegisterUpdateViewEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val updateViewUseCase: UpdateViewUseCase,
    private val formUpdateActionHandler: FormUpdateActionHandler,
    private val shadowerMaterialRepository: ShadowerMaterialRepositoryProtocol,
) : UseCaseProtocol.Sync<Unit, Unit> {

    override fun invoke(input: Unit) {
        coroutineScopes.launchOnEvent {
            formUpdateActionHandler.events
                .forever {
                    coroutineScopes.launchOnUiProcessor {
                        updateViewUseCase.invoke(it.url, it.jsonObject)
                    }
                }
        }

        coroutineScopes.launchOnEvent {
            shadowerMaterialRepository.events
                .filter { it.type == Shadower.Type.onDemandDefinition }
                .forever {
                    coroutineScopes.launchOnUiProcessor {
                        updateViewUseCase.invoke(it.url, it.jsonObject)
                    }
                }
        }
    }
}