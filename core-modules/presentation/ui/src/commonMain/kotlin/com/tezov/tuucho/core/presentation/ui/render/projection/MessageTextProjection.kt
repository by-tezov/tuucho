package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement

interface MessageTextProjectionProtocol : ProjectionProtocols<String>

class MessageTextProjection(
    key: String,
    storage: ProjectionStorageProtocol<String>,
) : MessageTextProjectionProtocol,
    ProjectionProtocols<String> by Projection(
        key = key,
        storage = storage,
    ) {
    lateinit var onReceived: ((String?) -> Unit)

    private val textProjection = createTextProjection(key, mutable = false, contextual = false)

    override suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ) = textProjection.getValueOrNull(jsonElement)

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        superProcess(jsonElement)
        onReceived.invoke(value)
    }
}

private class ContextualMessageTextProjection(
    private val delegate: MessageTextProjection
) : MessageTextProjectionProtocol by delegate,
    UpdatableProtocol {
    override val type = TypeSchema.Value.message

    override lateinit var id: String
        private set

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (!this::id.isInitialized) {
            jsonElement?.idValue?.let { id = it }
        }
        delegate.process(jsonElement)
    }
}

fun createMessageTextProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): MessageTextProjectionProtocol {
    val projection = when (mutable) {
        true -> MessageTextProjection(key, Projection.Mutable())
        false -> MessageTextProjection(key, Projection.Static())
    }
    return when {
        contextual -> ContextualMessageTextProjection(projection)
        else -> projection
    }
}
