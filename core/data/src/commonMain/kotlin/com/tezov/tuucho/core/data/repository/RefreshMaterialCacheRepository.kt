package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.source.RefreshMaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.RetrieveMaterialRemoteSource
import com.tezov.tuucho.core.data.source.RetrieveObjectRemoteSource
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

class RefreshMaterialCacheRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val retrieveObjectRemoteSource: RetrieveObjectRemoteSource,
    private val retrieveMaterialRemoteSource: RetrieveMaterialRemoteSource,
    private val refreshMaterialCacheLocalSource: RefreshMaterialCacheLocalSource,
) : MaterialRepositoryProtocol.RefreshCache {

    override suspend fun process(url: String) {
        val configModelDomain = retrieveObjectRemoteSource.process(url)
        coroutineScopes.parser.await {
            configModelDomain.onScope(ConfigSchema.Preload::Scope).let { preloadScope ->
                preloadScope.subs?.refreshCache()
                preloadScope.templates?.refreshCache()
                preloadScope.pages?.refreshCache()
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
                        materialObject = material,
                        url = url,
                        visibility = Visibility.Global,
                        lifetime = Lifetime.Unlimited
                    )
                }
            }
        }
    }

    private fun JsonElement.url() = withScope(ConfigSchema.MaterialItem::Scope).url
        ?: throw DataException.Default("missing url in page material $this")

}


