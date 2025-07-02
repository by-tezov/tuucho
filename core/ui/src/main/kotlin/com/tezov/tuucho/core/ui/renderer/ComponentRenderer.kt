package com.tezov.tuucho.core.ui.renderer

import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.id
import com.tezov.tuucho.core.domain.schema.SubsetSchema.Companion.subset
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.type
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

class ComponentRenderer(
    private val renderers: List<Renderer>
) : ScreenRendererProtocol, KoinComponent {

    override fun process(component: JsonElement): ScreenProtocol? {
        if(component.type != TypeSchema.Value.Type.component) {
            throw IllegalStateException("object is not a component $component")
        }
        val renderer = renderers
            .filter { it.accept(component) }
            .also {
                if (it.size > 1) {
                    throw IllegalStateException("Only one renderer can accept the object ${component.id} ${component.subset}")
                }
            }
            .firstOrNull()
        return renderer?.process(component).also {
                if(it == null) {
                    println("Warning, component in MaterialRenderer was not rendered $component")
                }
            }
    }

}