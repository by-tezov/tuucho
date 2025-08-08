package com.tezov.tuucho.core.data.parser.rectifier.dimensions

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.DimensionSchema
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

class DimensionsRectifier : Rectifier() {

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.DIMENSIONS
    )

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonArray {
        val output = mutableListOf<JsonObject>()
        element.find(path).jsonObject.forEach { (group, dimensions) ->
            dimensions.jsonObject.forEach { (key, dimension) ->
                when (dimension) {
                    is JsonPrimitive -> alterPrimitiveDimension(key, group, dimension)
                    is JsonObject -> alterObjectDimension(key, group, dimension)
                    else -> error("type not managed")
                }.let(output::add)
            }
        }
        return JsonArray(output)
    }

    private fun alterPrimitiveDimension(
        key: String,
        group: String,
        dimension: JsonPrimitive,
    ) = dimension.withScope(DimensionSchema::Scope).apply {
        type = TypeSchema.Value.dimension
        id = onScope(IdSchema::Scope).apply {
            value = key.addGroup(group)
        }.collect()
        default = this.element.string
    }.collect()

    private fun alterObjectDimension(
        key: String,
        group: String,
        dimension: JsonObject,
    ) = dimension.withScope(DimensionSchema::Scope).apply {
        type = TypeSchema.Value.dimension
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