package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.source.RefreshMaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.RetrieveMaterialRemoteSource
import com.tezov.tuucho.core.data.source.RetrieveObjectRemoteSource
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.setting.ConfigSchema
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

class RefreshCacheMaterialRepository(
    private val coroutineContextProvider: CoroutineContextProviderProtocol,
    private val retrieveObjectRemoteSource: RetrieveObjectRemoteSource,
    private val retrieveMaterialRemoteSource: RetrieveMaterialRemoteSource,
    private val refreshMaterialCacheLocalSource: RefreshMaterialCacheLocalSource,
) : RefreshCacheMaterialRepositoryProtocol {

    override suspend fun process(url: String) {
        val configModelDomain = retrieveObjectRemoteSource.process(url)
        withContext(coroutineContextProvider.default) {
            configModelDomain.withScope(ConfigSchema.Root::Scope).let { configScope ->
                configScope.preload?.withScope(ConfigSchema.Preload::Scope)?.let { preloadScope ->
                    preloadScope.subs?.refreshCache()
                    preloadScope.templates?.refreshCache()
                    preloadScope.pages?.refreshCache()
                }
            }
        }
    }

    private suspend fun JsonArray.refreshCache() {
        for (element in this) {
            val url = element.url()
            if (!refreshMaterialCacheLocalSource.shouldRefresh()) continue
            element.withScope(ConfigSchema.MaterialItem::Scope).let { subScope ->
                retrieveMaterialRemoteSource.process(url).let { material ->
                    refreshMaterialCacheLocalSource.process(
                        material = material,
                        url = url,
                        isShared = true
                    )
                }
            }
        }
    }

    private fun JsonElement.url() = withScope(ConfigSchema.MaterialItem::Scope).url
        ?: throw DataException.Default("missing url in page material $this")

}


