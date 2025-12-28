package com.tezov.tuucho.core.presentation.ui.render.projector

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.defaultStatus
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.ComponentProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.ProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

interface ComponentProjectorProtocols : ComponentProjectorProtocol, HasReadyStatusProtocol

@TuuchoUiDsl
class ComponentProjector : ComponentProjectorProtocols {
    private val projectors: MutableList<ProjectorProtocol> = mutableListOf()

    override var isReady by mutableStateOf(defaultStatus)

    override lateinit var onStatusChanged: () -> Unit

    override val updatables: List<UpdatableProtocol>
        get() = buildList {
            projectors.forEach {
                if (it is HasUpdatableProtocol) {
                    addAll(it.updatables)
                }
            }
        }

    override fun add(
        projector: ProjectorProtocol
    ) {
        projectors.add(projector)
        (projector as? HasReadyStatusProtocol)?.let { status ->
            status.onStatusChanged = {
                val previous = isReady
                val next = projectors.fold(defaultStatus) { acc, projection ->
                    if (projection is HasReadyStatusProtocol) {
                        acc && projection.isReady
                    } else {
                        acc
                    }
                }
                if (previous != next) {
                    isReady = next
                    if (this::onStatusChanged.isInitialized) {
                        onStatusChanged.invoke()
                    }
                }
            }
        }
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

private class ContextualComponentProjector(
    private val delegate: ComponentProjectorProtocols
) : ComponentProjectorProtocols by delegate,
    UpdatableProtocol {
    override val type = TypeSchema.Value.component

    override var id: String? = null
        private set

    override val updatables: List<UpdatableProtocol>
        get() = buildList {
            add(this@ContextualComponentProjector)
            addAll(delegate.updatables)
        }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (id == null) {
            jsonElement?.idValue?.let { id = it }
        }
        delegate.process(jsonElement)
    }
}

fun componentProjector(
    contextual: Boolean = false,
    block: ComponentProjectorProtocols.() -> Unit
): ComponentProjectorProtocols {
    val componentProjector = ComponentProjector().also {
        it.block()
    }
    return when {
        contextual -> ContextualComponentProjector(componentProjector)
        else -> componentProjector
    }
}
