package com.tezov.tuucho.core.presentation.ui.render.projection.message

import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projection.MutableStorageProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projector.MessageProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement

private typealias MessageIntTextTypeAlias = Int

private typealias MessageIntProjectionTypeAlias = ProjectionProtocols<MessageIntTextTypeAlias>

interface MessageIntProjectionProtocol :
    ResolveStatusProcessorProtocol,
    MessageIntProjectionTypeAlias

private class MessageIntProjection(
    private val projection: MessageIntProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : MessageIntProjectionProtocol,
    MessageIntProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status {
    private val intProjection =
        _root_ide_package_.com.tezov.tuucho.core.presentation.ui.render.projection.dimension.createIntProjection(
            key
        )

    init {
        attach(this as com.tezov.tuucho.core.presentation.ui.render.projection.ExtractorProjectionProtocol<MessageIntTextTypeAlias>)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        projection.process(jsonElement)
        status.update(jsonElement)
    }

    override suspend fun extract(
        jsonElement: JsonElement?
    ) = intProjection.extract(jsonElement)
}

private class MutableMessageIntProjection(
    delegate: MessageIntProjectionProtocol,
    storage: com.tezov.tuucho.core.presentation.ui.render.projection.StorageProjectionProtocol<MessageIntTextTypeAlias>
) : MessageIntProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

fun createMessageIntProjection(
    key: String
): MessageIntProjectionProtocol = MessageIntProjection(
    projection = _root_ide_package_.com.tezov.tuucho.core.presentation.ui.render.projection.Projection(
        key = key
    ),
    status = ResolveStatusProcessor()
)

val MessageIntProjectionProtocol.mutable
    get(): MessageIntProjectionProtocol = MutableMessageIntProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

fun MessageProjectorProtocols.int(
    key: String,
): MessageIntProjectionProtocol = createMessageIntProjection(key)
