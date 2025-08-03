package com.tezov.tuucho.core.data.parser.rectifier.colors

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.ColorSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
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

class ColorsRectifier : Rectifier() {

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.COLORS
    )

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonArray {
        val output = mutableListOf<JsonObject>()
        element.find(path).jsonObject.forEach { (group, colors) ->
            colors.jsonObject.forEach { (key, color) ->
                when (color) {
                    is JsonPrimitive -> alterPrimitiveColor(key, group, color)
                    is JsonObject -> alterObjectColor(key, group, color)
                    else -> error("type not managed")
                }.let(output::add)
            }
        }
        return JsonArray(output)
    }

    private fun alterPrimitiveColor(
        key: String,
        group: String,
        color: JsonPrimitive,
    ) = color.withScope(ColorSchema::Scope).apply {
        type = TypeSchema.Value.color
        id = onScope(IdSchema::Scope).apply {
            value = key.addGroup(group)
        }.collect()
        default = this.element.string
    }.collect()

    private fun alterObjectColor(
        key: String,
        group: String,
        color: JsonObject,
    ) = color.withScope(ColorSchema::Scope).apply {
        type = TypeSchema.Value.color
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