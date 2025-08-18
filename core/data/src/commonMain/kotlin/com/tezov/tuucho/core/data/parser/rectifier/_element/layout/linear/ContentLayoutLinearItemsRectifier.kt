package com.tezov.tuucho.core.data.parser.rectifier._element.layout.linear

import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class ContentLayoutLinearItemsRectifier : Rectifier() {

    private val matcher = ContentLayoutLinearItemsMatcher()

    override fun accept(path: JsonElementPath, element: JsonElement) = matcher.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = beforeAlterObject("".toPath(), element.find(path)
        .withScope(IdSchema::Scope).apply {
            self = this.element
        }
        .collect())

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList {
        add(element.find(path).jsonObject)
    }.let(::JsonArray)

    override fun beforeAlterArray(path: JsonElementPath, element: JsonElement): JsonElement? {
        val current = element.find(path).jsonArray
        if(!current.any { it is JsonPrimitive }) return null
        return current.map {
            if(it is JsonPrimitive) {
                it.withScope(IdSchema::Scope).apply {
                    self = this.element
                }.collect()
            } else it
        }.let(::JsonArray)
    }
}