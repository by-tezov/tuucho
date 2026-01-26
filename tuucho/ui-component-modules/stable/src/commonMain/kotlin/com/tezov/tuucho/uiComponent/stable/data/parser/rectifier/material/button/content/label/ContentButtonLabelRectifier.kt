package com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.button.content.label

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.component.ComponentRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ButtonSchema
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.LabelSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class ContentButtonLabelRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = ButtonSchema.Content.Key.label
    private val componentRectifier: ComponentRectifier by inject()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(ButtonSchema.Content.Key.label)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetOf(ButtonSchema.Component.Value.subset) &&
            parent.isTypeOf(TypeSchema.Value.content)
    }

    override fun beforeAlterPrimitive(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ) = beforeAlterObject(
        context,
        ROOT_PATH,
        element
            .find(path)
            .withScope(IdSchema::Scope)
            .apply {
                self = this.element
            }.collect()
    )

    override fun beforeAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? = element
        .find(path)
        .withScope(SubsetSchema::Scope)
        .takeIf { it.self == null }
        ?.apply { self = LabelSchema.Component.Value.subset }
        ?.collect()

    override fun afterAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ) = componentRectifier.process(
        context = context,
        path = ROOT_PATH,
        element = element.find(path).jsonObject
    )
}
