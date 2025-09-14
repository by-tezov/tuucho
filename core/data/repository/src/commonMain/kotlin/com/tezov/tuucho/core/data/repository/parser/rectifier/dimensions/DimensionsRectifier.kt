package com.tezov.tuucho.core.data.repository.parser.rectifier.dimensions

import com.tezov.tuucho.core.data.repository.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.repository.parser.rectifier.AbstractRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
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

class DimensionsRectifier : AbstractRectifier() {

    override val childProcessors: List<AbstractRectifier> by inject(
        Name.Processor.DIMENSIONS
    )

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonArray {
        return buildList {
            element.find(path).jsonObject.forEach { (group, dimensions) ->
                dimensions.jsonObject.forEach { (key, dimension) ->
                    when (dimension) {
                        is JsonPrimitive -> alterPrimitiveDimension(key, group, dimension)
                        is JsonObject -> alterObjectDimension(key, group, dimension)
                        else -> error("type not managed")
                    }.let(::add)
                }
            }
        }.let(::JsonArray)
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