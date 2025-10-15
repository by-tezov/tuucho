package com.tezov.tuucho.core.data.repository.parser.rectifier.style

import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class StylesRectifier : AbstractRectifier() {

    private val styleRectifier: StyleRectifier by inject()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList {
        element.find(path).jsonObject.forEach { (group, styles) ->
            styles.jsonObject.forEach { (key, style) ->
                when (style) {
                    is JsonPrimitive -> alterPrimitive(key, group, style)
                    is JsonObject -> alterObject(key, group, style)
                    else -> error("type not managed")
                }.let(::add)
            }
        }
    }.let(::JsonArray)

    private fun alterPrimitive(
        key: String,
        group: String,
        jsonPrimitive: JsonPrimitive,
    ) = jsonPrimitive.withScope(StyleSchema::Scope).apply {
        val stringValue = this.element.string.requireIsRef()
        id = onScope(IdSchema::Scope).apply {
            value = key.addGroup(group)
            source = stringValue
        }.collect()
    }.collect()

    private fun alterObject(
        key: String,
        group: String,
        jsonObject: JsonObject,
    ) = jsonObject.withScope(StyleSchema::Scope).apply {
        id = onScope(IdSchema::Scope).apply {
            when (val id = id) {
                is JsonNull, null -> value = key.addGroup(group)

                is JsonPrimitive -> {
                    source = id.stringOrNull?.requireIsRef()
                    value = key.addGroup(group)
                }

                is JsonObject -> {
                    source ?: run { source = value?.requireIsRef() }
                    value = key.addGroup(group)
                }

                else -> error("type not managed")
            }
        }.collect()
    }.collect()

    override fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).jsonArray.map {
        styleRectifier.process("".toPath(), it)
    }.let(::JsonArray)

}