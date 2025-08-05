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
        val materialObject = retrieveMaterialCacheLocalSource.process(url)
            ?: run {
                val material = retrieveMaterialRemoteSource.process(url)
                refreshMaterialCacheLocalSource.process(
                    materialObject = material,
                    url = url,
                    visibility = Visibility.Local,
                    lifetime = Lifetime.Unlimited
                )
                retrieveMaterialCacheLocalSource.process(url)
                    ?: throw DataException.Default("Retrieved url $url returned nothing")
            }
        return materialObject
    }
}
