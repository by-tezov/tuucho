package com.tezov.tuucho.core.data.repository.parser.rectifier.material.text

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.isEscapedRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.isRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema.Key
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class TextsRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = Key.texts
    private val textRectifier: TextRectifier by inject()

    override fun beforeAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList {
        element.find(path).jsonObject.forEach { (group, texts) ->
            texts.jsonObject.forEach { (key, text) ->
                when (text) {
                    is JsonPrimitive -> alterPrimitive(key, group, text)
                    is JsonObject -> alterObject(key, group, text)
                    else -> error("type not managed")
                }.let(::add)
            }
        }
    }.let(::JsonArray)

    private fun alterPrimitive(
        key: String,
        group: String,
        jsonPrimitive: JsonPrimitive,
    ) = jsonPrimitive
        .withScope(TextSchema::Scope)
        .apply {
            val stringValue = element.string
            val isEscapedRef = stringValue.isEscapedRef
            val isRef = stringValue.isRef && !stringValue.isEscapedRef
            if (isRef) {
                id = onScope(IdSchema::Scope)
                    .apply {
                        value = key.addGroup(group)
                        source = stringValue
                    }.collect()
            } else {
                id = key.addGroup(group).let(::JsonPrimitive)
                default = if (isEscapedRef) {
                    stringValue.drop(1)
                } else {
                    stringValue
                }
            }
        }.collect()

    private fun alterObject(
        key: String,
        group: String,
        jsonObject: JsonObject,
    ) = jsonObject
        .withScope(TextSchema::Scope)
        .apply {
            id = onScope(IdSchema::Scope)
                .apply {
                    when (val id = id) {
                        is JsonNull, null -> {
                            value = key.addGroup(group)
                        }

                        is JsonPrimitive -> {
                            source = id.stringOrNull?.requireIsRef()
                            value = key.addGroup(group)
                        }

                        is JsonObject -> {
                            source ?: run { source = value?.requireIsRef() }
                            value = key.addGroup(group)
                        }

                        else -> {
                            error("type not managed")
                        }
                    }
                }.collect()
        }.collect()

    override fun afterAlterArray(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ) = element
        .find(path)
        .jsonArray
        .map {
            textRectifier.process(context, ROOT_PATH, it)
        }.let(::JsonArray)
}
