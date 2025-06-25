package com.tezov.tuucho.core.data.parser.rectifier.dimensions

import android.util.MalformedJsonException
import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.DimensionSchema.Companion.defaultPut
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idAddGroup
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idIsRef
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idPutObject
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idPutPrimitive
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idRawOrNull
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idSourceOrNull
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idValueOrNull
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typePut
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
        element: JsonElement
    ): JsonArray {
        val output = mutableListOf<JsonObject>()
        element.find(path).jsonObject.forEach { (group, dimensions) ->
            dimensions.jsonObject.forEach { (key, dimension) ->
                when (dimension) {
                    is JsonPrimitive -> alterPrimitiveDimension(key, group, dimension.string)
                    is JsonObject -> alterObjectDimension(key, group, dimension)
                    else -> throw MalformedJsonException("type not managed")
                }.let(output::add)
            }
        }
        return JsonArray(output)
    }

    private fun alterPrimitiveDimension(
        key: String,
        group: String,
        dimension: String
    ) = mutableMapOf<String, JsonElement>()
        .apply {
            typePut(TypeSchema.Value.Type.dimension)
            idPutPrimitive(key.idAddGroup(group))
            defaultPut(dimension)
        }
        .let(::JsonObject)

    private fun alterObjectDimension(
        key: String,
        group: String,
        dimension: JsonObject
    ) = dimension.toMutableMap().apply {
        typePut(TypeSchema.Value.Type.dimension)
        when (val _id = idRawOrNull) {
            is JsonNull, null -> idPutPrimitive(key.idAddGroup(group))

            is JsonPrimitive -> idPutObject(
                key.idAddGroup(group), _id.stringOrNull?.requireIsRef()
            )

            is JsonObject -> idPutObject(
                key.idAddGroup(group), (idSourceOrNull ?: idValueOrNull?.requireIsRef())
            )

            else -> throw MalformedJsonException("type not managed")
        }
    }.let(::JsonObject)

    private fun String.requireIsRef(): String {
        if (!idIsRef) {
            throw MalformedJsonException("should start with *")
        }
        return this
    }

}