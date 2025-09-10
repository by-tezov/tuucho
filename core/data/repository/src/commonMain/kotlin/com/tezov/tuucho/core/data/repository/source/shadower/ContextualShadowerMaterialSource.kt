package com.tezov.tuucho.core.data.repository.source.shadower

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import com.tezov.tuucho.core.data.repository.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.source.MaterialRemoteSource
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.coroutines.awaitAll
import kotlinx.serialization.json.JsonObject
import kotlin.time.Clock

class ContextualShadowerMaterialSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialCacheLocalSource: MaterialCacheLocalSource,
    private val materialRemoteSource: MaterialRemoteSource,
    private val materialAssembler: MaterialAssembler,
    private val materialDatabaseSource: MaterialDatabaseSource,
) : ShadowerMaterialSourceProtocol {

    override val type = Shadower.Type.contextual

    override val isCancelled = false

    private lateinit var urlOrigin: String
    private lateinit var map: MutableMap<String, MutableList<JsonObject>>

    override suspend fun onStart(url: String, materialElement: JsonObject) {
        this.urlOrigin = url
        map = mutableMapOf()
    }

    override suspend fun onNext(jsonObject: JsonObject) {
        val idScope = jsonObject.onScope(IdSchema::Scope)
        idScope.source ?: return
        val url = jsonObject.onScope(ComponentSettingSchema::Scope)
            .urlContextual?.replace("\${current}", urlOrigin)
            ?: "$urlOrigin${ComponentSettingSchema.Value.UrlContextual.suffix}"
        map[url] = (map[url] ?: mutableListOf()).apply { add(jsonObject) }
    }

    override suspend fun onDone() = map.map { (url, jsonObjects) ->
        coroutineScopes.parser.async {
            downloadAndCache(url)
            jsonObjects.assembleAll(url)
        }
    }.awaitAll().flatten()

    private suspend fun downloadAndCache(url: String) {
        val lifetime = materialCacheLocalSource.getLifetime(url)
        if (materialCacheLocalSource.isCacheValid(url, lifetime?.validityKey)) {
            return
        }
        val remoteMaterialObject = materialRemoteSource.process(url)
        materialCacheLocalSource.delete(url, Table.Contextual)
        materialCacheLocalSource.insert(
            materialObject = remoteMaterialObject,
            url = url,
            weakLifetime = if (lifetime == null || lifetime is Lifetime.Enrolled) {
                Lifetime.Transient(
                    validityKey = lifetime?.validityKey,
                    expirationDateTime = Clock.System.now()
                )
            } else lifetime,
            visibility = Visibility.Contextual(urlOrigin = urlOrigin)
        )
    }

    private suspend fun List<JsonObject>.assembleAll(url: String) = mapNotNull { jsonObject ->
        materialAssembler.process(
            materialObject = jsonObject,
            findAllRefOrNullFetcher = { from, type ->
                coroutineScopes.database.await {
                    materialDatabaseSource.getAllRefOrNull(
                        from = from,
                        url = url,
                        type = type,
                        visibility = Visibility.Contextual(urlOrigin = urlOrigin)
                    )
                }
            }
        )
    }
}
