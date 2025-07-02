package com.tezov.tuucho.core.data.parser.rectifier._element.field

import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.schema.TextSchema.defaultPut
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class ContentFieldTextErrorRectifier : Rectifier() {

    private val matcher = ContentFieldTextErrorMatcher()

    override fun accept(path: JsonElementPath, element: JsonElement) = matcher.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ) = beforeAlterObject("".toPath(), mutableMapOf<String, JsonElement>().apply {
        defaultPut(element.find(path).string)
    }.let(::JsonObject))

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = mutableListOf<JsonElement>().apply {
        add(element.find(path).jsonObject)
    }.let(::JsonArray)

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? {
        val jsonArray = element.find(path).jsonArray
        if(!jsonArray.any { it is JsonPrimitive }) return null
        return JsonArray(jsonArray.map {
            if (it is JsonPrimitive) {
                mutableMapOf<String, JsonElement>().apply {
                    defaultPut(it.string)
                }.let(::JsonObject)
            } else {
                it.jsonObject
            }
        })
    }

}
