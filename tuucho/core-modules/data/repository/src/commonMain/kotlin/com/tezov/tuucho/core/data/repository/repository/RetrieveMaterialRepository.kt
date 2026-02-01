package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.JsonLifetime
import com.tezov.tuucho.core.data.repository.database.type.JsonVisibility
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.repository.source.MaterialRemoteSource
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

internal class RetrieveMaterialRepository(
    private val materialCacheLocalSource: MaterialCacheLocalSource,
    private val materialRemoteSource: MaterialRemoteSource,
) : MaterialRepositoryProtocol.Retrieve {
    override suspend fun process(
        url: String
    ): JsonObject {
        val lifetime = materialCacheLocalSource.getLifetime(url)
        if (materialCacheLocalSource.isCacheValid(url, lifetime?.validityKey)) {
            materialCacheLocalSource.assemble(url)?.let {
                if (lifetime is JsonLifetime.SingleUse) {
                    materialCacheLocalSource.delete(url, Table.Common)
                }
                return it
            }
        }
        val remoteMaterialObject = materialRemoteSource.process(url)
        materialCacheLocalSource.delete(url, Table.Common)
        materialCacheLocalSource.insert(
            materialObject = remoteMaterialObject,
            url = url,
            weakLifetime = if (lifetime == null || lifetime is JsonLifetime.Enrolled) {
                JsonLifetime.Unlimited(
                    validityKey = lifetime?.validityKey
                )
            } else {
                lifetime
            },
            visibility = JsonVisibility.Local
        )

        return materialCacheLocalSource.assemble(url).also {
            val lifetime = materialCacheLocalSource.getLifetime(url)
            if (lifetime is JsonLifetime.SingleUse) {
                materialCacheLocalSource.delete(url, Table.Common)
            }
        } ?: throw DataException.Default("Retrieved url $url returned nothing")
    }
}
