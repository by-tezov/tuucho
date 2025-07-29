package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.source.RefreshMaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.RetrieveMaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.RetrieveMaterialRemoteSource
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialRepository(
    private val retrieveMaterialCacheLocalSource: RetrieveMaterialCacheLocalSource,
    private val retrieveMaterialRemoteSource: RetrieveMaterialRemoteSource,
    private val refreshMaterialCacheLocalSource: RefreshMaterialCacheLocalSource,
    private val shadowerMaterialRepository: ShadowerMaterialRepository,
) : RetrieveMaterialRepositoryProtocol {

    override suspend fun process(url: String): JsonObject {
        val materialElement = retrieveMaterialCacheLocalSource.process(url)
            ?: run {
                val material = retrieveMaterialRemoteSource.process(url)
                refreshMaterialCacheLocalSource.process(
                    material = material,
                    url = url,
                    isShared = false
                )
                retrieveMaterialCacheLocalSource.process(url)
                    ?: throw DataException.Default("Retrieved url $url returned nothing")
            }
        shadowerMaterialRepository.process(url, materialElement)
        return materialElement
    }


}
