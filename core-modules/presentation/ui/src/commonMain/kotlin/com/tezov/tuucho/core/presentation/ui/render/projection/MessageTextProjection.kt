package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement

private typealias MessageTextProjectionProtocols = ProjectionProtocols<String>

interface MessageTextProjectionProtocol : MessageTextProjectionProtocols {
    var onReceived: ((String?) -> Unit)
}

class MessageTextProjection(
    private val projection: MessageTextProjectionProtocols,
) : MessageTextProjectionProtocol,
    MessageTextProjectionProtocols by projection {
    override lateinit var onReceived: ((String?) -> Unit)

    private val textProjection = createTextProjection(key, mutable = false, contextual = false)

    init {
        attach(this)
    }

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
    val projection: MessageTextProjectionProtocols = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val messageTextProjection = MessageTextProjection(projection)
    return when {
        contextual -> ContextualMessageTextProjection(messageTextProjection)
        else -> messageTextProjection
    }
}
