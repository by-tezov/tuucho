package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.source.MaterialRemoteSource
import com.tezov.tuucho.core.data.repository.source.RetrieveObjectRemoteSource
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrueOrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray

class RefreshMaterialCacheRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val retrieveObjectRemoteSource: RetrieveObjectRemoteSource,
    private val materialRemoteSource: MaterialRemoteSource,
    private val materialCacheLocalSource: MaterialCacheLocalSource,
) : MaterialRepositoryProtocol.RefreshCache {

    override suspend fun process(url: String) {
        val configModelDomain = retrieveObjectRemoteSource.process(url)
        coroutineScopes.parser.await {
            configModelDomain.onScope(ConfigSchema.MaterialResource::Scope)
                .let { materialResourceScope ->
                    materialResourceScope.global?.refreshGlobalCache()
                    materialResourceScope.local?.refreshLocalCache()
                    materialResourceScope.contextual?.refreshContextualCache()
                }
        }
    }

    private suspend fun downloadAndCache(
        url: String,
        validityKey: String,
        visibility: Visibility,
    ) {
        materialRemoteSource.process(url).let { material ->
            materialCacheLocalSource.insert(
                materialObject = material,
                url = url,
                weakLifetime = Lifetime.Unlimited(validityKey = validityKey),
                visibility = visibility
            )
        }
    }

    private suspend fun enroll(
        url: String,
        validityKey: String,
        visibility: Visibility,
    ) {
        materialCacheLocalSource.enroll(
            url = url,
            validityKey = validityKey,
            visibility = visibility
        )
    }

    private suspend fun JsonObject.refreshGlobalCache() {
        forEach { (_, value) ->
            for (element in value.jsonArray) {
                element.withScope(ConfigSchema.MaterialItem::Scope).let { configScope ->
                    val url = configScope.url
                        ?: throw DataException.Default("missing url in global material $this")
                    val validityKey = configScope.validityKey
                        ?: throw DataException.Default("missing validity key in global material $this")
                    if (materialCacheLocalSource.isCacheValid(url, validityKey)) continue
                    materialCacheLocalSource.delete(url, Table.Common)
                    if (configScope.preDownload.isTrueOrNull) {
                        downloadAndCache(url, validityKey, Visibility.Global)
                    } else {
                        enroll(url, validityKey, Visibility.Global)
                    }
                }
            }
        }
    }

    private suspend fun JsonObject.refreshLocalCache() {
        forEach { (_, value) ->
            for (element in value.jsonArray) {
                element.withScope(ConfigSchema.MaterialItem::Scope).let { configScope ->
                    val url = configScope.url
                        ?: throw DataException.Default("missing url in local material $this")
                    val validityKey = configScope.validityKey
                        ?: throw DataException.Default("missing validity key in local material $this")
                    if (materialCacheLocalSource.isCacheValid(url, validityKey)) continue
                    materialCacheLocalSource.delete(url, Table.Common)
                    if (configScope.preDownload.isTrueOrNull) {
                        downloadAndCache(url, validityKey, Visibility.Local)
                    } else {
                        enroll(url, validityKey, Visibility.Local)
                    }
                }
            }
        }
    }

    private suspend fun JsonObject.refreshContextualCache() {
        forEach { (_, value) ->
            for (element in value.jsonArray) {
                element.withScope(ConfigSchema.MaterialItem::Scope).let { configScope ->
                    val urlOrigin = configScope.urlOrigin
                        ?: throw DataException.Default("missing urlOrigin in contextual material $this")
                    val url = configScope.url
                        ?: "${urlOrigin}${ComponentSettingSchema.Value.UrlContextual.suffix}"
                    val validityKey = configScope.validityKey
                        ?: throw DataException.Default("missing validity key in contextual material $this")
                    if (materialCacheLocalSource.isCacheValid(url, validityKey)) continue
                    materialCacheLocalSource.delete(url, Table.Contextual)
                    enroll(url, validityKey, Visibility.Contextual(urlOrigin = urlOrigin))
                }
            }
        }
    }

}


