package com.tezov.tuucho.core.presentation.ui.render.projector

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.ComponentProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@TuuchoUiDsl
class TypeProjector(
    override val type: String
) : TypeProjectorProtocol {
    private val projectables: MutableList<ProjectableProtocol> = mutableListOf()

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
    private val delegate: TypeProjectorProtocol
) : TypeProjectorProtocol by delegate,
    UpdatableProtocol {
    override val type get() = delegate.type

    override lateinit var id: String
        private set

    override val updatables: List<UpdatableProtocol>
        get() = buildList {
            add(this@ContextualTypeProjector)
            addAll(delegate.updatables)
        }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (!this::id.isInitialized) {
            jsonElement?.idValue?.let { id = it }
        }
        delegate.process(jsonElement)
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
): TypeProjectorProtocol {
    val typeProjector = TypeProjector(type = TypeSchema.Value.content).also {
        add(it)
        it.block()
    }
    return when {
        contextual -> ContextualTypeProjector(typeProjector)
        else -> typeProjector
    }
}

fun ComponentProjectorProtocol.state(
    contextual: Boolean = false,
    block: TypeProjectorProtocol.() -> Unit
): TypeProjectorProtocol {
    val typeProjector = TypeProjector(type = TypeSchema.Value.state).also {
        add(it)
        it.block()
    }
    return when {
        contextual -> ContextualTypeProjector(typeProjector)
        else -> typeProjector
    }
}
