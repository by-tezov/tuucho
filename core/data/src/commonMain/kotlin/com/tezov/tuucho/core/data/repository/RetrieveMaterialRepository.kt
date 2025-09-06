package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.source.RefreshMaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.RetrieveMaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.RetrieveMaterialRemoteSource
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialRepository(
    private val retrieveMaterialCacheLocalSource: RetrieveMaterialCacheLocalSource,
    private val retrieveMaterialRemoteSource: RetrieveMaterialRemoteSource,
    private val refreshMaterialCacheLocalSource: RefreshMaterialCacheLocalSource,
) : MaterialRepositoryProtocol.Retrieve {

    override suspend fun process(url: String): JsonObject {
        if (refreshMaterialCacheLocalSource.isCacheValid(url, null)){
            retrieveMaterialCacheLocalSource.process(url)?.let {
                return it
            }
        }
        val remoteMaterialObject = retrieveMaterialRemoteSource.process(url)
        refreshMaterialCacheLocalSource.process(
            materialObject = remoteMaterialObject,
            url = url,
            validityKey = null,
            visibility = Visibility.Local,
            lifetime = Lifetime.Unlimited
        )
        return retrieveMaterialCacheLocalSource.process(url)
            ?: throw DataException.Default("Retrieved url $url returned nothing")
    }
}
