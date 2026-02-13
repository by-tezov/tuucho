package com.tezov.tuucho.core.data.repository.parser.rectifier.material.config

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema.MaterialResource.Contextual
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema.MaterialResource.Global
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema.MaterialResource.Local
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

@OpenForTest
internal class ConfigRectifier(
    private val coroutineScopes: CoroutineScopesProtocol
) {

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        configObject: JsonObject
    ) = coroutineScopes.default.withContext {
        configObject
            .withScope(ConfigSchema::Scope)
            .apply {
                materialResource?.let { materialResource = it.rectifyMaterialResource() }
            }.collect()
    }

    private fun JsonObject.rectifyMaterialResource() = withScope(ConfigSchema.MaterialResource::Scope).apply {
        global?.let { global = it.rectifyMaterialResourceGlobal() }
        local?.let { local = it.rectifyMaterialResourceLocal() }
        contextual?.let { contextual = it.rectifyMaterialResourceContextual() }
    }.collect()

    private fun JsonObject.rectifyMaterialResourceGlobal(): JsonObject {
        val setting = onScope(Global.Setting::Scope).apply {
            urlsWhiteList = when (val urlsWhiteList = this[Global.Setting.Key.urlsWhiteList]) {
                is JsonPrimitive -> JsonArray(listOf(urlsWhiteList))
                is JsonArray -> urlsWhiteList
                else -> throw DataException.Default("invalid urlsWhiteList format")
            }
        }
        return (this - Global.Setting.root).mapValues { (_, element) ->
            element.jsonArray.map { item ->
                item.jsonObject.withScope(Global.Setting::Scope).apply {
                    urlsWhiteList = when (val urlsWhiteList = this[Global.Setting.Key.urlsWhiteList]) {
                        null, is JsonNull -> setting.urlsWhiteList
                        is JsonPrimitive -> JsonArray(listOf(urlsWhiteList))
                        is JsonArray -> urlsWhiteList
                        else -> throw DataException.Default("invalid urlsWhiteList format")
                    }
                }.collect()
            }.let(::JsonArray)
        }.let(::JsonObject)
    }

    private fun JsonObject.rectifyMaterialResourceLocal(): JsonObject {
        val setting = onScope(Local.Setting::Scope)
        return (this - Local.Setting.root).mapValues { (_, element) ->
            element.jsonArray.map { item ->
                item.jsonObject.withScope(Local.Setting::Scope).apply {
                    if (preDownload == null && setting.preDownload != null) {
                        preDownload = setting.preDownload
                    }
                }.collect()
            }.let(::JsonArray)
        }.let(::JsonObject)
    }

    private fun JsonObject.rectifyMaterialResourceContextual(): JsonObject {
        val setting = onScope(Contextual.Setting::Scope)
        return (this - Contextual.Setting.root).mapValues { (_, element) ->
            element.jsonArray.map { item ->
                item.jsonObject.withScope(Contextual.Setting::Scope).apply {
                    if (urlOrigin == null && setting.urlOrigin != null) {
                        urlOrigin = setting.urlOrigin
                    }
                }.collect()
            }.let(::JsonArray)
        }.let(::JsonObject)
    }

}
