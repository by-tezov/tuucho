package com.tezov.tuucho.core.presentation.ui.renderer.screen

import com.tezov.tuucho.core.domain.business.model.schema._system.onScope
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.renderer.screen._system.ScreenIdentifierFactory
import com.tezov.tuucho.core.presentation.ui.renderer.screen._system.ScreenProtocol
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewFactory
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScreenRenderer(
    private val identifierFactory: ScreenIdentifierFactory,
) : ScreenRendererProtocol, KoinComponent {

    private val viewFactories: List<ViewFactory> by inject()

    override suspend fun process(componentObject: JsonObject): ScreenProtocol {
        val type = componentObject.withScope(TypeSchema::Scope).self
        if (type != TypeSchema.Value.component) {
            throw UiException.Default("object is not a component $componentObject")
        }
        val id = componentObject.onScope(IdSchema::Scope).value
        val subset = componentObject.withScope(SubsetSchema::Scope).self

        val screenIdentifier = identifierFactory()
        val viewFactory = viewFactories
            .filter { it.accept(componentObject) }
            .singleOrThrow(id, subset)
            ?: throw UiException.Default("No renderer found for $componentObject")

        return Screen(
            view = viewFactory.process(screenIdentifier, componentObject),
            identifier = screenIdentifier
        )
    }

    private fun <T> List<T>.singleOrThrow(id: String?, subset: String?): T? {
        if (size > 1) throw UiException.Default("Only one renderer can accept the object $id $subset")
        return firstOrNull()
    }

}


