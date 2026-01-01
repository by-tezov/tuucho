package com.tezov.tuucho.core.presentation.ui.render.projection.message

import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projection.Projection
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.ValueProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.createTextProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.MessageProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement

private typealias MessageTextTypeAlias = String

private typealias MessageTextProjectionTypeAlias = ProjectionProtocols<String>

interface MessageTextProjectionProtocol :
    ResolveStatusProcessorProtocol,
    MessageTextProjectionTypeAlias

private class MessageTextProjection(
    private val projection: MessageTextProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : MessageTextProjectionProtocol,
    MessageTextProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status {
    private val textProjection = createTextProjection(key)

    init {
        attach(this as ValueProjectionProtocol<MessageTextTypeAlias>)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        projection.process(jsonElement)
        status.update(jsonElement)
    }

    override suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ) = textProjection.getValueOrNull(jsonElement)
}

fun createMessageTextProjection(
    key: String
): MessageTextProjectionProtocol = MessageTextProjection(
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
)

fun MessageProjectorProtocols.text(
    key: String,
): MessageTextProjectionProtocol = createMessageTextProjection(key)
