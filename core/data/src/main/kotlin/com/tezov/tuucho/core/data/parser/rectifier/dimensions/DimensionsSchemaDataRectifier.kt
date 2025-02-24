package com.tezov.tuucho.core.data.parser.rectifier.dimensions

import android.util.MalformedJsonException
import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser.SchemaDataRectifier
import com.tezov.tuucho.core.data.parser._schema.DimensionSchemaData
import com.tezov.tuucho.core.data.parser._schema.DimensionSchemaData.Companion.defaultPut
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idAddGroup
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idIsRef
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idPutObject
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idPutPrimitive
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idRawOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idSourceOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idValueOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData.Companion.typePut
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.domain.model._system.string
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

object DimensionsSchemaDataRectifier : SchemaDataRectifier() {

    override val matchers: List<SchemaDataMatcher> by inject(
        Name.Matcher.DIMENSIONS
    )

    override val childProcessors: List<SchemaDataRectifier> by inject(
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
            typePut(DimensionSchemaData.Default.type)
            idPutPrimitive(key.idAddGroup(group))
            defaultPut(dimension)
        }
        .let(::JsonObject)

    private fun alterObjectDimension(
        key: String,
        group: String,
        dimension: JsonObject
    ) = dimension.toMutableMap().apply {
        typePut(DimensionSchemaData.Default.type)
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