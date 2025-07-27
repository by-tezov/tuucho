package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.database.MaterialCacheSource
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.assembler._system.ArgumentAssembler
import com.tezov.tuucho.core.data.parser.breaker._system.ArgumentBreaker
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.setting.ConfigSchema
import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

class RefreshCacheMaterialRepository(
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialCacheSource: MaterialCacheSource,
) : RefreshCacheMaterialRepositoryProtocol {

    override suspend fun refreshCache(url: String) {
        val configModelDomain = materialNetworkSource.retrieveConfig(url)
        configModelDomain.withScope(ConfigSchema.Root::Scope).let { configScope ->
            configScope.preload?.withScope(ConfigSchema.Preload::Scope)?.let { preloadScope ->
                preloadScope.subs?.refreshCache()
                preloadScope.templates?.refreshCache()
                preloadScope.pages?.refreshCache()
            }
        }
    }

    private suspend fun JsonArray.refreshCache() {
        for (element in this) {
            val url = element.url()
            if (!materialCacheSource.shouldRefresh()) continue
            element.withScope(ConfigSchema.MaterialItem::Scope).let { subScope ->
                materialNetworkSource.retrieve(url).let { materialElement ->
                    val version = element.version()
                    materialCacheSource.refreshCache(
                        version = version,
                        argumentAssembler = ArgumentAssembler(url),
                        argumentBreaker = ArgumentBreaker(url, true),
                        materialElement = materialElement
                    )
                }
            }
        }
    }

    private fun JsonElement.url() = withScope(ConfigSchema.MaterialItem::Scope).url
        ?: throw DataException.Default("missing url in page material $this")

    private fun JsonElement.version() = withScope(ConfigSchema.MaterialItem::Scope).version
        ?: throw DataException.Default("missing version in page material $this")

}


