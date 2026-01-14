package com.tezov.tuucho.core.presentation.ui.render.projection.message

import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projection.ExtractorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.MutableStorageProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.Projection
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.StorageProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.createTextProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.MessageProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement

private typealias MessageTextTypeAlias = String

private typealias MessageTextProjectionTypeAlias = ProjectionProtocols<MessageTextTypeAlias>

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
        attach(this as ExtractorProjectionProtocol<MessageTextTypeAlias>)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        projection.process(jsonElement)
        status.update(jsonElement)
    }

    override suspend fun extract(
        jsonElement: JsonElement?
    ) = textProjection.extract(jsonElement)
}

private class MutableMessageTextProjection(
    delegate: MessageTextProjectionProtocol,
    storage: StorageProjectionProtocol<MessageTextTypeAlias>
) : MessageTextProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

fun createMessageTextProjection(
    key: String
): MessageTextProjectionProtocol = MessageTextProjection(
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
)

val MessageTextProjectionProtocol.mutable
    get(): MessageTextProjectionProtocol = MutableMessageTextProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

fun MessageProjectorProtocols.text(
    key: String,
): MessageTextProjectionProtocol = createMessageTextProjection(key)
