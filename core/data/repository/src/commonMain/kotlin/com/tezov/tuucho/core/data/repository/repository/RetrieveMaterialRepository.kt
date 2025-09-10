package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.source.MaterialRemoteSource
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

class RetrieveMaterialRepository(
    private val materialCacheLocalSource: MaterialCacheLocalSource,
    private val materialRemoteSource: MaterialRemoteSource,
) : MaterialRepositoryProtocol.Retrieve {

    override suspend fun process(url: String): JsonObject {
        val lifetime = materialCacheLocalSource.getLifetime(url)
        if (materialCacheLocalSource.isCacheValid(url, lifetime?.validityKey)) {
            materialCacheLocalSource.assemble(url)?.let {
                return it
            }
        }
        val remoteMaterialObject = materialRemoteSource.process(url)
        materialCacheLocalSource.delete(url, Table.Common)
        materialCacheLocalSource.insert(
            materialObject = remoteMaterialObject,
            url = url,
            weakLifetime = if (lifetime == null || lifetime is Lifetime.Enrolled) {
                Lifetime.Unlimited(
                    validityKey = lifetime?.validityKey
                )
            } else lifetime,
            visibility = Visibility.Local
        )
        return materialCacheLocalSource.assemble(url)
            ?: throw DataException.Default("Retrieved url $url returned nothing")
    }

}
