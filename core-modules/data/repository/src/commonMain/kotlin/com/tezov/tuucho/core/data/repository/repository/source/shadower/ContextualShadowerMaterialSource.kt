package com.tezov.tuucho.core.data.repository.repository.source.shadower

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import com.tezov.tuucho.core.data.repository.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.repository.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.repository.source.MaterialRemoteSource
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower.Contextual
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower.Contextual.replaceUrlOriginToken
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.coroutines.awaitAll
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal class ContextualShadowerMaterialSource(
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

    override suspend fun onNext(jsonObject: JsonObject, settingObject: JsonObject?) {
        val idScope = jsonObject.onScope(IdSchema::Scope)
        idScope.source ?: return
        val type = jsonObject.withScope(TypeSchema::Scope).self
        val url = idScope.urlSource?.jsonObject
            ?.get(this.type)?.stringOrNull?.replaceUrlOriginToken(urlOrigin)
            ?: settingObject?.withScope(SettingComponentShadowerSchema.Contextual::Scope)
                ?.url?.get(type).stringOrNull?.replaceUrlOriginToken(urlOrigin)
            ?: Contextual.defaultUrl(urlOrigin)
        map[url] = (map[url] ?: mutableListOf()).apply { add(jsonObject) }
    }

    override suspend fun onDone() = map.map { (url, jsonObjects) ->
        coroutineScopes.parser.async {
            downloadAndCache(url)
            jsonObjects.assembleAll(url).also {
                val lifetime = materialCacheLocalSource.getLifetime(url)
                if (lifetime is Lifetime.SingleUse) {
                    materialCacheLocalSource.delete(url, Table.Common)
                }
            }
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
                Lifetime.SingleUse(
                    validityKey = lifetime?.validityKey
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
