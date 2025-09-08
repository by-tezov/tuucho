package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.MaterialRemoteSource
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialRepository(
    private val materialCacheLocalSource: MaterialCacheLocalSource,
    private val materialRemoteSource: MaterialRemoteSource,
) : MaterialRepositoryProtocol.Retrieve {

    override suspend fun process(url: String): JsonObject {
        val lifetime = materialCacheLocalSource.getLifetime(url)
        if (materialCacheLocalSource.isCacheValid(url, lifetime?.validityKey)) {
            materialCacheLocalSource.read(url)?.let {
                return it
            }
        }
        val remoteMaterialObject = materialRemoteSource.process(url)
        materialCacheLocalSource.delete(url)
        materialCacheLocalSource.insert(
            materialObject = remoteMaterialObject,
            url = url,
            weakLifetime = lifetime ?: Lifetime.Unlimited(validityKey = null),
            visibility = Visibility.Local
        )
        return materialCacheLocalSource.read(url)
            ?: throw DataException.Default("Retrieved url $url returned nothing")
    }

}
