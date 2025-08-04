package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.StateViewProtocol

class GetViewStateUseCase(
    private val viewStackRepository: ViewContextStackRepositoryProtocol,
) {

    fun invoke(url: String): StateViewProtocol {
        return viewStackRepository.getViewState(url)
    }
}