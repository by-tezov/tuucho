package com.tezov.tuucho.core.data.repository.parser.rectifier._element.layout.linear

import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.component.ComponentRectifier
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject

class ContentLayoutLinearItemsRectifier : AbstractRectifier() {

    private val componentRectifier: ComponentRectifier by inject()

    private val matcher = ContentLayoutLinearItemsMatcher()

    override fun accept(path: JsonElementPath, element: JsonElement) = matcher.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList { add(element.find(path)) }.let(::JsonArray)

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList { add(element.find(path)) }.let(::JsonArray)

    override fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).jsonArray.map {
        componentRectifier.process("".toPath(), it)
    }.let(::JsonArray)

}