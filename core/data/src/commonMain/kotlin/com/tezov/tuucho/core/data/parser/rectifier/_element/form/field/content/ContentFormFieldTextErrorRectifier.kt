package com.tezov.tuucho.core.data.parser.rectifier._element.form.field.content

import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business._system.find
import com.tezov.tuucho.core.domain.business._system.string
import com.tezov.tuucho.core.domain.business._system.toPath
import com.tezov.tuucho.core.domain.business.model.schema._system.SymbolData
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.TextSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class ContentFormFieldTextErrorRectifier : Rectifier() {

    private val matcher = ContentFormFieldTextErrorMatcher()

    override fun accept(path: JsonElementPath, element: JsonElement) = matcher.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = beforeAlterObject(
        "".toPath(), element.find(path).withScope(TextSchema::Scope).apply {
            val value = this.element.string
            //TODO add escaper on "ID_REF_INDICATOR" to allow string user content to start with it
            if (value.startsWith(SymbolData.ID_REF_INDICATOR)) {
                id = JsonPrimitive(value)
            } else {
                id = JsonNull
                default = value
            }
        }.collect()
    )

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = mutableListOf<JsonElement>().apply {
        add(element.find(path).jsonObject)
    }.let(::JsonArray)

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        val jsonArray = element.find(path).jsonArray
        if (!jsonArray.any { it is JsonPrimitive }) return null
        return JsonArray(jsonArray.map {
            if (it is JsonPrimitive) {
                it.withScope(TextSchema::Scope).apply {
                    default = this.element.string
                }.collect()
            } else {
                it.jsonObject
            }
        })
    }

}
