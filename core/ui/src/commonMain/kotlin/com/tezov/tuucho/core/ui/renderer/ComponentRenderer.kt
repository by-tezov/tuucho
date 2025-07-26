package com.tezov.tuucho.core.ui.renderer


import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

class ComponentRenderer(
    private val renderers: List<Renderer>,
) : ScreenRendererProtocol, KoinComponent {

    override fun process(component: JsonElement): ScreenProtocol? {
        val id = component.onScope(IdSchema::Scope).value
        val type = component.withScope(TypeSchema::Scope).self
        val subset = component.withScope(SubsetSchema::Scope).self

        if (type != TypeSchema.Value.component) {
            error("object is not a component $component")
        }
        val renderer = renderers
            .filter { it.accept(component) }
            .also {
                if (it.size > 1) {
                    error("Only one renderer can accept the object $id $subset")
                }
            }
            .firstOrNull()
        return renderer?.process(component).also {
            if (it == null) {
                println("Warning, component in MaterialRenderer was not rendered $component")
            }
        }
    }

}