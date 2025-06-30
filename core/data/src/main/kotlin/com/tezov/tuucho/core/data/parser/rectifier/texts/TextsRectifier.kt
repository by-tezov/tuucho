package com.tezov.tuucho.core.data.parser.rectifier.texts

import android.util.MalformedJsonException
import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idAddGroup
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idIsRef
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idPutObject
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idPutPrimitive
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idRawOrNull
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idSourceOrNull
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.idValueOrNull
import com.tezov.tuucho.core.domain.schema.TextSchema.defaultPut
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.typePut
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
        element: JsonElement
    ): JsonArray {
        val output = mutableListOf<JsonObject>()
        element.find(path).jsonObject.forEach { (group, texts) ->
            texts.jsonObject.forEach { (key, text) ->
                when (text) {
                    is JsonPrimitive -> alterPrimitiveText(key, group, text.string)
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
        text: String
    ) = mutableMapOf<String, JsonElement>()
        .apply {
            typePut(TypeSchema.Value.Type.text)
            idPutPrimitive(key.idAddGroup(group))
            defaultPut(text)
        }
        .let(::JsonObject)

    private fun alterObjectText(
        key: String,
        group: String,
        text: JsonObject
    ) = text.toMutableMap().apply {
        typePut(TypeSchema.Value.Type.text)
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