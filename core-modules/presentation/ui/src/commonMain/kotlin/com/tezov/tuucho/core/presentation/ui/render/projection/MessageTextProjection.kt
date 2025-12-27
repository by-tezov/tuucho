package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement

private typealias MessageTextProjectionTypeAlias = ProjectionProtocols<String>

interface MessageTextProjectionProtocol : MessageTextProjectionTypeAlias {
    var componentId: String
    var onReceived: ((String?) -> Unit)
}

class MessageTextProjection(
    private val projection: MessageTextProjectionTypeAlias,
) : MessageTextProjectionProtocol,
    MessageTextProjectionTypeAlias by projection {

    override lateinit var componentId: String

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

    override val id get() = delegate.componentId
}

fun createMessageTextProjection(
    key: String,
    mutable: Boolean
): MessageTextProjectionProtocol {
    val projection: MessageTextProjectionTypeAlias = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val messageTextProjection = MessageTextProjection(projection)
    return ContextualMessageTextProjection(messageTextProjection)
}
