package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveLocalImageUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.coroutines.flow.Flow

@OpenForTest
class RetrieveLocalImageUseCase(
    private val imagesRepository: ImageRepositoryProtocol.Local,
) : UseCaseProtocol.Sync<Input, Flow<ImageRepositoryProtocol.Image<*>>> {
    data class Input(
        val url: String,
    )

    override fun invoke(
        input: Input
    ) = with(input) {
        imagesRepository.process<Any>(url)
    }
}
