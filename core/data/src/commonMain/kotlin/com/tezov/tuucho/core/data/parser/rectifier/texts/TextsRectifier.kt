package com.tezov.tuucho.core.data.parser.rectifier.texts

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class TextsRectifier : Rectifier() {

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.TEXTS
    )

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonArray {
        return buildList {
            element.find(path).jsonObject.forEach { (group, texts) ->
                texts.jsonObject.forEach { (key, text) ->
                    when (text) {
                        is JsonPrimitive -> alterPrimitiveText(key, group, text)
                        is JsonObject -> alterObjectText(key, group, text)
                        else -> error("type not managed")
                    }.let(::add)
                }
            }
        }.let(::JsonArray)
    }

    private fun alterPrimitiveText(
        key: String,
        group: String,
        text: JsonPrimitive,
    ) = text.withScope(TextSchema::Scope).apply {
        type = TypeSchema.Value.text
        id = onScope(IdSchema::Scope).apply {
            value = key.addGroup(group)
        }.collect()
        default = this.element.string
    }.collect()

    private fun alterObjectText(
        key: String,
        group: String,
        text: JsonObject,
    ) = text.withScope(TextSchema::Scope).apply {
        type = TypeSchema.Value.text
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

}