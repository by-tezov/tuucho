package com.tezov.tuucho.core.presentation.ui.viewFactory


import com.tezov.tuucho.core.domain.business.model.schema._system.onScope
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.protocol.ComponentRendererProtocol
import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.state.AddViewUseCase
import com.tezov.tuucho.core.presentation.ui.viewFactory._system.ViewFactory
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class ComponentRendererFactory(
    private val addView: AddViewUseCase,
    private val uiComponentFactory: List<ViewFactory>,
) : ComponentRendererProtocol, KoinComponent {

    override fun process(url:String, componentObject: JsonObject): ViewProtocol? {
        val id = componentObject.onScope(IdSchema::Scope).value
        val type = componentObject.withScope(TypeSchema::Scope).self
        val subset = componentObject.withScope(SubsetSchema::Scope).self

        if (type != TypeSchema.Value.component) {
            error("object is not a component $componentObject")
        }

        val renderer = uiComponentFactory
            .filter { it.accept(componentObject) }
            .also {
                if (it.size > 1) {
                    error("Only one renderer can accept the object $id $subset")
                }
            }
            .firstOrNull()

        return renderer?.process(url, componentObject)
            .also {
                if (it == null) {
                    println("Warning, component was not rendered $componentObject")
                }
            }?.also { addView.invoke(url, it) }
    }

}