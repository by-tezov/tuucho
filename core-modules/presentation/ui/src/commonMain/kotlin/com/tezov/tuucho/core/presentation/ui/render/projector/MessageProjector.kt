package com.tezov.tuucho.core.presentation.ui.render.projector

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.subsetOrNull
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.MessageProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

interface MessageProjectorProtocols : MessageProjectorProtocol, HasUpdatableProtocol

@TuuchoUiDsl
class MessageProjector(
    override val subset: String
) : MessageProjectorProtocols {
    private val projectables: MutableList<ProjectableProtocol> = mutableListOf()

    override val type = TypeSchema.Value.message

    override val updatables: List<UpdatableProtocol>
        get() = buildList {
            projectables.forEach {
                if (it is HasUpdatableProtocol) {
                    addAll(it.updatables)
                }
            }
        }

    override fun add(
        projectable: ProjectableProtocol
    ) {
        projectables.add(projectable)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (jsonElement !is JsonObject) return
        val subset = jsonElement.subsetOrNull ?: return
        if (subset != this.subset) return
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

fun ComponentProjectorProtocols.message(
    subset: String,
    block: MessageProjectorProtocols.() -> Unit
): MessageProjectorProtocols = MessageProjector(subset)
    .also {
        add(it)
        it.block()
    }
