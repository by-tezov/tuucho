@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier._element.button.content.label

import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.component.ComponentRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.LabelSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

@OptIn(TuuchoExperimentalAPI::class)
class ContentButtonLabelRectifier : AbstractRectifier() {
    override val key = ButtonSchema.Content.Key.label
    private val matcher = ContentButtonLabelMatcher()
    private val componentRectifier: ComponentRectifier by inject()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = beforeAlterObject(
        "".toPath(),
        element
            .find(path)
            .withScope(IdSchema::Scope)
            .apply {
                self = this.element
            }.collect()
    )

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? = element
        .find(path)
        .withScope(ButtonSchema.Content::Scope)
        .takeIf { it.subset == null }
        ?.apply { subset = LabelSchema.Component.Value.subset }
        ?.collect()

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = componentRectifier.process(
        path = "".toPath(),
        element = element.find(path).jsonObject
    )
}
