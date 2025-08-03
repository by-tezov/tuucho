package com.tezov.tuucho.core.data.parser.rectifier._element.layout.linear

import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business._system.find
import com.tezov.tuucho.core.domain.business._system.toPath
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope

import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
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
    ) = mutableListOf<JsonElement>().apply {
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