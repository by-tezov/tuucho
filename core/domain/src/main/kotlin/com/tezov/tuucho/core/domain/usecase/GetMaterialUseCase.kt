package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.model.material.MaterialModelDomain
import com.tezov.tuucho.core.domain.repository.MaterialRepository

class GetMaterialUseCase(private val repository: MaterialRepository) {

    suspend operator fun invoke(url: String): MaterialModelDomain = repository.retrieve(url)

}