package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.source.ClearTransientMaterialCacheLocalSource
import com.tezov.tuucho.core.domain.protocol.ClearTransientMaterialCacheRepositoryProtocol

class ClearTransientMaterialCacheRepository(
    private val clearTransientMaterialCacheLocalSource: ClearTransientMaterialCacheLocalSource,
) : ClearTransientMaterialCacheRepositoryProtocol {

    override suspend fun process(urlOrigin: String) {
        clearTransientMaterialCacheLocalSource.process(urlOrigin)
    }
}


