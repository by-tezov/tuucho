package com.tezov.tuucho.core.presentation.ui.render.projector

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

interface TypeProjectorProtocols : TypeProjectorProtocol, HasUpdatableProtocol, ReadyStatusProtocol

@TuuchoUiDsl
class TypeProjector(
    override val type: String
) : TypeProjectorProtocols {
    private val projectables: MutableList<ProjectableProtocol> = mutableListOf()

    override var isReady = false
        private set

    override lateinit var onStatusChanged: () -> Unit

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
        (projectable as? ReadyStatusProtocol)?.let { status ->
            status.onStatusChanged = {
                val previous = isReady
                isReady = isReady && status.isReady
                if (previous != isReady && this::onStatusChanged.isInitialized) {
                    onStatusChanged.invoke()
                }
            }
        }
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

private class ContextualTypeProjector(
    private val delegate: TypeProjectorProtocols
) : TypeProjectorProtocols by delegate,
    UpdatableProtocol {

    override val type get() = delegate.type

    override var id: String? = null
        private set

    override val updatables: List<UpdatableProtocol>
        get() = buildList {
            add(this@ContextualTypeProjector)
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

fun ComponentProjectorProtocols.option(
    block: TypeProjectorProtocols.() -> Unit
): TypeProjectorProtocols = TypeProjector(type = TypeSchema.Value.option)
    .also {
        add(it)
        it.block()
    }

fun ComponentProjectorProtocols.style(
    contextual: Boolean = false,
    block: TypeProjectorProtocols.() -> Unit
): TypeProjectorProtocols {
    val typeProjector = TypeProjector(type = TypeSchema.Value.style).also {
        it.block()
    }
    return when {
        contextual -> ContextualTypeProjector(typeProjector)
        else -> typeProjector
    }.also { add(it) }
}

fun ComponentProjectorProtocols.content(
    contextual: Boolean = false,
    block: TypeProjectorProtocols.() -> Unit
): TypeProjectorProtocols {
    val typeProjector = TypeProjector(type = TypeSchema.Value.content).also {
        it.block()
    }
    return when {
        contextual -> ContextualTypeProjector(typeProjector)
        else -> typeProjector
    }.also { add(it) }
}

fun ComponentProjectorProtocols.state(
    contextual: Boolean = false,
    block: TypeProjectorProtocols.() -> Unit
): TypeProjectorProtocols {
    val typeProjector = TypeProjector(type = TypeSchema.Value.state).also {
        it.block()
    }
    return when {
        contextual -> ContextualTypeProjector(typeProjector)
        else -> typeProjector
    }.also { add(it) }
}
