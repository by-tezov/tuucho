package com.tezov.tuucho.core.ui.renderer

import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idObject
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idValue
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subset
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.type
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreen
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class MaterialRenderer(
    private val renderers: List<Renderer>
) : KoinComponent {

    fun process(component: JsonObject): ComposableScreen? {
        if(component.type != TypeSchema.Value.Type.component) {
            throw IllegalStateException("object is not a component $component")
        }
        val renderer = renderers
            .filter { it.accept(component) }
            .also {
                if (it.size > 1) {
                    throw IllegalStateException("Only one renderer can accept the object ${component.idObject.idValue} ${component.subset}")
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