package com.tezov.tuucho.core.ui.uiComponentFactory


import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.usecase.state.AddScreenInMaterialStateUseCase
import com.tezov.tuucho.core.ui.uiComponentFactory._system.UiComponentFactory
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class MaterialUiComponentFactory(
    private val addScreenInMaterialState: AddScreenInMaterialStateUseCase,
    private val uiComponentFactory: List<UiComponentFactory>,
) : ScreenRendererProtocol, KoinComponent {

    override fun process(url:String, componentElement: JsonObject): ScreenProtocol? {
        val id = componentElement.onScope(IdSchema::Scope).value
        val type = componentElement.withScope(TypeSchema::Scope).self
        val subset = componentElement.withScope(SubsetSchema::Scope).self

        if (type != TypeSchema.Value.component) {
            error("object is not a component $componentElement")
        }

        val renderer = uiComponentFactory
            .filter { it.accept(componentElement) }
            .also {
                if (it.size > 1) {
                    error("Only one renderer can accept the object $id $subset")
                }
            }
            .firstOrNull()

        return renderer?.process(url, componentElement)
            .also {
                if (it == null) {
                    println("Warning, component was not rendered $componentElement")
                }
            }?.also { addScreenInMaterialState.invoke(url, it) }
    }

}