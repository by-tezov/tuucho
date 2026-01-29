package com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.layout.linear

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.component.ComponentRectifier
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.layout.LayoutLinearSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class ContentLayoutLinearItemsRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = LayoutLinearSchema.Content.Key.items
    private val componentRectifier: ComponentRectifier by inject()

    private val matcher = ContentLayoutLinearItemsRectifierMatcher()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)

    override fun beforeAlterPrimitive(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList { add(element.find(path)) }.let(::JsonArray)

    override fun beforeAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList { add(element.find(path)) }.let(::JsonArray)

    override fun afterAlterArray(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ) = element
        .find(path)
        .jsonArray
        .map {
            componentRectifier.process(context, ROOT_PATH, it)
        }.let(::JsonArray)
}
