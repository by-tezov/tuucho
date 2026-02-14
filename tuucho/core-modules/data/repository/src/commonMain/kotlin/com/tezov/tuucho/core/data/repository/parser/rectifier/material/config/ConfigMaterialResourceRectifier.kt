package com.tezov.tuucho.core.data.repository.parser.rectifier.material.config

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema.MaterialResource
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema.MaterialResource.Contextual
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema.MaterialResource.Global
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema.MaterialResource.Local
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray

@OpenForTest
internal class ConfigMaterialResourceRectifier {
    private val globalResourceRectifier by lazy { GlobalResourceRectifier() }
    private val localResourceRectifier by lazy { LocalResourceRectifier() }
    private val contextualResourceRectifier by lazy { ContextualResourceRectifier() }

    fun process(
        materialResourceObject: JsonObject
    ) = materialResourceObject
        .withScope(MaterialResource::Scope)
        .apply {
            global?.let { global = globalResourceRectifier.process(it) }
            local?.let { local = localResourceRectifier.process(it) }
            contextual?.let { contextual = contextualResourceRectifier.process(it) }
        }.collect()
}

private abstract class AbstractResourceRectifier<S : OpenSchemaScope<S>, D : OpenSchemaScope<D>> {
    fun process(
        resourceObject: JsonObject
    ): JsonObject {
        val baseSetting = resourceObject.rectifyOnSetting(base = null)
        return (resourceObject - rootSettingKey)
            .mapValues { (_, element) ->
                when (element) {
                    is JsonObject -> {
                        val elementSetting = element.rectifyOnSetting(base = baseSetting)
                        element
                            .onScope(scopeDefinitionFactory)
                            .element
                            .rectifyDefinitions(setting = elementSetting)
                    }

                    is JsonArray -> {
                        element.rectifyDefinitions(setting = baseSetting)
                    }

                    else -> {
                        throw DataException.Default("invalid material resource entry")
                    }
                }
            }.let(::JsonObject)
    }

    protected abstract val rootSettingKey: String

    protected abstract val scopeSettingFactory: (argument: SchemaScopeArgument) -> S

    protected abstract val scopeDefinitionFactory: (argument: SchemaScopeArgument) -> D

    private fun JsonElement.rectifyDefinitions(
        setting: S
    ) = jsonArray
        .map {
            it
                .withScope(scopeDefinitionFactory)
                .rectifyDefinition(setting = setting)
        }.let(::JsonArray)

    private fun JsonObject.rectifyOnSetting(
        base: S?
    ) = onScope(scopeSettingFactory).rectifySetting(base = base)

    protected abstract fun S.rectifySetting(
        base: S?
    ): S

    protected abstract fun D.rectifyDefinition(
        setting: S
    ): JsonObject
}

private class GlobalResourceRectifier : AbstractResourceRectifier<Global.Setting.Scope, Global.Definition.Scope>() {
    override val rootSettingKey: String
        get() = Global.Setting.root

    override val scopeSettingFactory
        get() = Global.Setting::Scope

    override val scopeDefinitionFactory
        get() = Global.Definition::Scope

    private fun Global.Setting.Scope.rectifyUrlsWhiteList(
        defaultUrlsWhiteList: JsonArray?
    ) {
        urlsWhiteList = when (val urlsWhiteList = this[Global.Setting.Key.urlsWhiteList]) {
            null, JsonNull -> defaultUrlsWhiteList
            is JsonPrimitive -> JsonArray(listOf(urlsWhiteList))
            is JsonArray -> urlsWhiteList
            else -> throw DataException.Default("invalid urlsWhiteList format")
        }
    }

    private fun Global.Setting.Scope.rectifyValidityKey(
        defaultValidityKey: String?
    ) {
        defaultValidityKey?.let { baseValidityKey ->
            validityKey = validityKey?.let { "$baseValidityKey-$it" } ?: baseValidityKey
        }
    }

    override fun Global.Setting.Scope.rectifySetting(
        base: Global.Setting.Scope?
    ) = also {
        rectifyUrlsWhiteList(base?.urlsWhiteList)
        rectifyValidityKey(base?.validityKey)
    }

    override fun Global.Definition.Scope.rectifyDefinition(
        setting: Global.Setting.Scope
    ) = also {
        if (element is JsonPrimitive) {
            url = element.stringOrNull
        }
        withScope(Global.Setting::Scope).rectifySetting(setting)
    }.collect()
}

private class LocalResourceRectifier : AbstractResourceRectifier<Local.Setting.Scope, Local.Definition.Scope>() {
    override val rootSettingKey: String
        get() = Local.Setting.root

    override val scopeSettingFactory
        get() = Local.Setting::Scope

    override val scopeDefinitionFactory
        get() = Local.Definition::Scope

    private fun Local.Setting.Scope.rectifyPreDownload(
        defaultPreDownload: Boolean?
    ) {
        if (preDownload == null && defaultPreDownload != null) {
            preDownload = defaultPreDownload
        }
    }

    private fun Local.Setting.Scope.rectifyValidityKey(
        defaultValidityKey: String?
    ) {
        defaultValidityKey?.let { baseValidityKey ->
            validityKey = validityKey?.let { "$baseValidityKey-$it" } ?: baseValidityKey
        }
    }

    override fun Local.Setting.Scope.rectifySetting(
        base: Local.Setting.Scope?
    ) = also {
        rectifyPreDownload(base?.preDownload)
        rectifyValidityKey(base?.validityKey)
    }

    override fun Local.Definition.Scope.rectifyDefinition(
        setting: Local.Setting.Scope
    ) = also {
        if (element is JsonPrimitive) {
            url = element.stringOrNull
        }
        withScope(Local.Setting::Scope).rectifySetting(setting)
    }.collect()
}

private class ContextualResourceRectifier : AbstractResourceRectifier<Contextual.Setting.Scope, Contextual.Definition.Scope>() {
    override val rootSettingKey: String
        get() = Contextual.Setting.root

    override val scopeSettingFactory
        get() = Contextual.Setting::Scope

    override val scopeDefinitionFactory
        get() = Contextual.Definition::Scope

    private fun Contextual.Setting.Scope.rectifyPreDownload(
        defaultUrlOrigin: String?
    ) {
        if (urlOrigin == null && defaultUrlOrigin != null) {
            urlOrigin = defaultUrlOrigin
        }
    }

    private fun Contextual.Setting.Scope.rectifyValidityKey(
        defaultValidityKey: String?
    ) {
        defaultValidityKey?.let { baseValidityKey ->
            validityKey = validityKey?.let { "$baseValidityKey-$it" } ?: baseValidityKey
        }
    }

    override fun Contextual.Setting.Scope.rectifySetting(
        base: Contextual.Setting.Scope?
    ) = also {
        rectifyPreDownload(base?.urlOrigin)
        rectifyValidityKey(base?.validityKey)
    }

    override fun Contextual.Definition.Scope.rectifyDefinition(
        setting: Contextual.Setting.Scope
    ) = also {
        if (element is JsonPrimitive) {
            url = element.stringOrNull
        }
        withScope(Contextual.Setting::Scope).rectifySetting(setting)
    }.collect()
}
