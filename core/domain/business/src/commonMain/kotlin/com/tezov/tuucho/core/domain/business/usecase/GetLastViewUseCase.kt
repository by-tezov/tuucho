package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol

class GetLastViewUseCase(
    private val viewStackRepository: ViewContextStackRepositoryProtocol,
) {

    fun invoke(): ViewProtocol {
        return viewStackRepository.currentViewContext.view
    }
}