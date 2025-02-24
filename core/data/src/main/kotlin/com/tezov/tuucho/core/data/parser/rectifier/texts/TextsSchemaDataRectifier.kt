package com.tezov.tuucho.core.data.parser.rectifier.texts

import android.util.MalformedJsonException
import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.SchemaDataMatcher
import com.tezov.tuucho.core.data.parser.SchemaDataRectifier
import com.tezov.tuucho.core.data.parser._schema.TextSchemaData
import com.tezov.tuucho.core.data.parser._schema.TextSchemaData.Companion.defaultPut
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

object TextsSchemaDataRectifier : SchemaDataRectifier() {

    override val matchers: List<SchemaDataMatcher> by inject(
        Name.Matcher.TEXTS
    )

    override val childProcessors: List<SchemaDataRectifier> by inject(
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
            typePut(TextSchemaData.Default.type)
            idPutPrimitive(key.idAddGroup(group))
            defaultPut(text)
        }
        .let(::JsonObject)

    private fun alterObjectText(
        key: String,
        group: String,
        text: JsonObject
    ) = text.toMutableMap().apply {
        typePut(TextSchemaData.Default.type)
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