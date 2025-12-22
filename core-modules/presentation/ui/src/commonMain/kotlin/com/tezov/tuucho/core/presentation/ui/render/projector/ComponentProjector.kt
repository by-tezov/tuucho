package com.tezov.tuucho.core.presentation.ui.render.projector

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.render.protocol.ComponentProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@TuuchoUiDsl
class ComponentProjector : ComponentProjectorProtocol {

    private val projectors: MutableList<ProjectorProtocol> = mutableListOf()

    override fun add(
        projector: ProjectorProtocol
    ) {
        projectors.add(projector)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (jsonElement !is JsonObject) return
        projectors.forEach { projector ->
            jsonElement[projector.type]
                ?.let { childJsonElement -> projector.process(childJsonElement) }
        }
    }
}

fun componentProjector(
    contextual: Boolean = false,
    block: ComponentProjectorProtocol.() -> Unit
): ComponentProjectorProtocol = ComponentProjector()
    .also { it.block() }

val ComponentProjectorProtocol.contextual
    get(): ComponentProjectorProtocol = object : ComponentProjectorProtocol, UpdatableProtocol {
        override val type = TypeSchema.Value.component

        override lateinit var id: String
            private set

        override fun add(
            projector: ProjectorProtocol
        ) = this@contextual.add(projector)

        override suspend fun process(
            jsonElement: JsonElement?
        ) {
            if (!this::id.isInitialized) {
                jsonElement?.idValue?.let { id = it }
            }
            this@contextual.process(jsonElement)
        }
    }
