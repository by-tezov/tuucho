package com.tezov.tuucho.core.data.repository.repository.source.shadower

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.JsonLifetime
import com.tezov.tuucho.core.data.repository.database.type.JsonVisibility
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerProtocol
import com.tezov.tuucho.core.data.repository.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.repository.source.MaterialRemoteSource
import com.tezov.tuucho.core.data.repository.repository.source.shadower.ContextualShadowerMaterialSource.Context
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower.Contextual.replaceUrlOriginToken
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal class ContextualShadowerMaterialSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialCacheLocalSource: MaterialCacheLocalSource,
    private val materialRemoteSource: MaterialRemoteSource,
    private val materialAssembler: MaterialAssembler,
    private val materialDatabaseSource: MaterialDatabaseSource,
) : ShadowerMaterialSourceProtocol<Context> {
    data class Context(
        val urlOrigin: String,
        val urlContextualFallback: (type: String) -> String,
        val map: MutableMap<String, MutableList<JsonObject>> = mutableMapOf()
    ) : ShadowerMaterialSourceProtocol.Context

    override val type = SettingComponentShadowerSchema.Key.contextual

    override fun accept(
        url: String,
        setting: JsonObject?,
        componentObject: JsonObject
    ) = true

    override fun createContext(
        url: String,
        setting: JsonObject?,
        componentObject: JsonObject
    ) = Context(
        urlOrigin = url,
        urlContextualFallback = { type ->
            setting
                ?.withScope(SettingComponentShadowerSchema.Contextual::Scope)
                ?.url
                ?.get(type)
                .stringOrNull
                ?.replaceUrlOriginToken(url)
                ?: Shadower.Contextual.defaultUrl(url)
        }
    )

    override fun process(
        context: Context,
        jsonObject: JsonObject
    ) {
        with(context) {
            val idScope = jsonObject.onScope(IdSchema::Scope)
            idScope.source ?: return
            val url = idScope.urlSource
                ?.jsonObject
                ?.get(type)
                .stringOrNull
                ?.replaceUrlOriginToken(urlOrigin)
                ?: urlContextualFallback(jsonObject.withScope(TypeSchema::Scope).self ?: throw DataException.Default("type is null"))
            map.getOrPut(url) { mutableListOf() }.add(jsonObject)
        }
    }

    override suspend fun finalize(
        context: Context
    ) = context.map
        .map { (url, jsonObjects) ->
            coroutineScopes.default.async {
                downloadAndCache(url, context.urlOrigin)
                jsonObjects.assembleAll(url, context.urlOrigin).also {
                    val lifetime = materialCacheLocalSource.getLifetime(url)
                    if (lifetime is JsonLifetime.SingleUse) {
                        materialCacheLocalSource.delete(url, Table.Common)
                    }
                }
            }
        }.awaitAll()
        .flatten()
        .asFlow()

    private suspend fun downloadAndCache(
        url: String,
        urlOrigin: String
    ) {
        val lifetime = materialCacheLocalSource.getLifetime(url)
        if (materialCacheLocalSource.isCacheValid(url, lifetime?.validityKey)) {
            return
        }
        val remoteMaterialObject = materialRemoteSource.process(url)
        materialCacheLocalSource.delete(url, Table.Contextual)
        materialCacheLocalSource.insert(
            materialObject = remoteMaterialObject,
            url = url,
            urlWhiteList = null,
            weakLifetime = if (lifetime == null || lifetime is JsonLifetime.Enrolled) {
                JsonLifetime.SingleUse(
                    validityKey = lifetime?.validityKey
                )
            } else {
                lifetime
            },
            visibility = JsonVisibility.Contextual(urlOrigin = urlOrigin)
        )
    }

    private suspend fun List<JsonObject>.assembleAll(
        url: String,
        urlOrigin: String
    ) = mapNotNull { jsonObject ->
        materialAssembler.process(
            context = AssemblerProtocol.Context(
                url = url,
                findAllRefOrNullFetcher = { from, type ->
                    materialDatabaseSource.getAllRefOrNull(
                        from = from,
                        url = url,
                        type = type,
                        visibility = JsonVisibility.Contextual(urlOrigin = urlOrigin)
                    )
                }
            ),
            materialObject = jsonObject
        )
    }
}
