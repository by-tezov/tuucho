package com.tezov.tuucho.core.ui.renderer

import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
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
        val schema = component.schema()
        val id = schema.withScope(IdSchema::Scope).self.stringOrNull
        val type = schema.withScope(TypeSchema::Scope).self
        val subset = schema.withScope(SubsetSchema::Scope).self

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