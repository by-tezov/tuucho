package com.tezov.tuucho.core.data.parser.rectifier.colors

import android.util.MalformedJsonException
import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.ColorSchema.defaultPut
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idAddGroup
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idIsRef
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idPutObject
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idPutPrimitive
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idRawOrNull
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idSourceOrNull
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idValueOrNull
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.typePut
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
        element: JsonElement
    ): JsonArray {
        val output = mutableListOf<JsonObject>()
        element.find(path).jsonObject.forEach { (group, colors) ->
            colors.jsonObject.forEach { (key, color) ->
                when (color) {
                    is JsonPrimitive -> alterPrimitiveColor(key, group, color.string)
                    is JsonObject -> alterObjectColor(key, group, color)
                    else -> throw MalformedJsonException("type not managed")
                }.let(output::add)
            }
        }
        return JsonArray(output)
    }

    private fun alterPrimitiveColor(
        key: String,
        group: String,
        color: String
    ) = mutableMapOf<String, JsonElement>()
        .apply {
            typePut(TypeSchema.Value.Type.color)
            idPutPrimitive(key.idAddGroup(group))
            defaultPut(color)
        }
        .let(::JsonObject)

    private fun alterObjectColor(
        key: String,
        group: String,
        color: JsonObject
    ) = color.toMutableMap().apply {
        typePut(TypeSchema.Value.Type.color)
        when (val _id = JsonObject(this).idRawOrNull) {
            is JsonNull, null -> idPutPrimitive(key.idAddGroup(group))

            is JsonPrimitive -> idPutObject(
                key.idAddGroup(group), _id.stringOrNull?.requireIsRef()
            )

            is JsonObject -> idPutObject(
                key.idAddGroup(group), (JsonObject(this).idSourceOrNull ?: JsonObject(this).idValueOrNull?.requireIsRef())
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