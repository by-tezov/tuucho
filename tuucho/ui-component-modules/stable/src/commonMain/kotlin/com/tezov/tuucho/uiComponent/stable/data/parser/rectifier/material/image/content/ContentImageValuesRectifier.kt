package com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.image.content

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.image.ImageRectifier
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ImageSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class ContentImageValuesRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = ImageSchema.Content.Key.values
    private val imageRectifier: ImageRectifier by inject()

    private val matcher = ContentImageValuesRectifierMatcher()

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
            imageRectifier.process(context, ROOT_PATH, it)
        }.let(::JsonArray)
}
