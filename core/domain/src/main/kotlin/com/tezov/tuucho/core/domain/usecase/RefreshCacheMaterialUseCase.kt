package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.repository.MaterialRepository

class RefreshCacheMaterialUseCase(private val repository: MaterialRepository) {

    suspend operator fun invoke(url: String) = repository.refreshCache(url)

}