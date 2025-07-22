package com.tezov.tuucho.core.data.parser.rectifier.texts

import android.util.MalformedJsonException
import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.model.schema.material.TextSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
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
        val output = mutableListOf<JsonObject>()
        element.find(path).jsonObject.forEach { (group, texts) ->
            texts.jsonObject.forEach { (key, text) ->
                when (text) {
                    is JsonPrimitive -> alterPrimitiveText(key, group, text)
                    is JsonObject -> alterObjectText(key, group, text)
                    else -> throw MalformedJsonException("type not managed")
                }.let(output::add)
            }
        }
        return JsonArray(output)
    }

    private fun alterPrimitiveText(
        key: String,
        group: String,
        text: JsonPrimitive,
    ) = text.schema().withScope(TextSchema::Scope).apply {
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
    ) = text.schema().withScope(TextSchema::Scope).apply {
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

                else -> throw MalformedJsonException("type not managed")
            }
        }.collect()
    }.collect()

}