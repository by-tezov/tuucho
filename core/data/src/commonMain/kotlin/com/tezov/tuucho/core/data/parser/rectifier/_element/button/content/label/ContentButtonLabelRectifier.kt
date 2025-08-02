package com.tezov.tuucho.core.data.parser.rectifier._element.button.content.label

import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LabelSchema
import kotlinx.serialization.json.JsonElement

class ContentButtonLabelRectifier : Rectifier() {

    private val matcher = ContentButtonLabelMatcher()

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
    ): JsonElement? = element.find(path)
        .withScope(ButtonSchema.Content::Scope)
        .takeIf { it.subset == null }
        ?.apply { subset = LabelSchema.Component.Value.subset }
        ?.collect()
}