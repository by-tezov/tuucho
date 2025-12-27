package com.tezov.tuucho.core.presentation.ui.render.projector

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.render.protocol.ComponentProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@TuuchoUiDsl
class TypeProjector(
    override val type: String
) : TypeProjectorProtocol {
    private val projectables: MutableList<ProjectableProtocol> = mutableListOf()

    override fun add(
        projectable: ProjectableProtocol
    ) {
        projectables.add(projectable)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (jsonElement !is JsonObject) return
        projectables.forEach { projectable ->
            projectable.keys.forEach { key ->
                jsonElement[key]
                    ?.let { childJsonElement ->
                        projectable.process(childJsonElement, key)
                    }
            }
        }
    }
}

fun ComponentProjectorProtocol.style(
    block: TypeProjectorProtocol.() -> Unit
): TypeProjectorProtocol = TypeProjector(type = TypeSchema.Value.style)
    .also {
        add(it)
        it.block()
    }

fun ComponentProjectorProtocol.option(
    block: TypeProjectorProtocol.() -> Unit
): TypeProjectorProtocol = TypeProjector(type = TypeSchema.Value.option)
    .also {
        add(it)
        it.block()
    }

fun ComponentProjectorProtocol.content(
    contextual: Boolean = false,
    block: TypeProjectorProtocol.() -> Unit
): TypeProjectorProtocol = TypeProjector(type = TypeSchema.Value.content)
    .also {
        add(it)
        it.block()
    }

fun ComponentProjectorProtocol.state(
    block: TypeProjectorProtocol.() -> Unit
): TypeProjectorProtocol = TypeProjector(type = TypeSchema.Value.state)
    .also {
        add(it)
        it.block()
    }
