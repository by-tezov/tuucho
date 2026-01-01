package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias TextTypeAlias = String

private typealias TextProjectionTypeAlias = ProjectionProtocols<TextTypeAlias>

interface TextProjectionProtocol :
    IdProcessorProtocol,
    TextProjectionTypeAlias,
    ResolveStatusProcessorProtocol

private class TextProjection(
    private val idProcessor: IdProcessorProtocol,
    private val projection: TextProjectionTypeAlias,
    private val statusProcessor: ResolveStatusProcessorProtocol
) : TextProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    TextProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by statusProcessor {
    init {
        attach(this as ExtractorProjectionProtocol<TextTypeAlias>)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        idProcessor.process(jsonElement)
        projection.process(jsonElement)
        statusProcessor.update(jsonElement)
    }

    override suspend fun extract(
        jsonElement: JsonElement?
    ) = when (jsonElement) {
        is JsonObject -> {
            jsonElement
                .withScope(TextSchema::Scope)
                .default
        }

        is JsonPrimitive -> {
            jsonElement.stringOrNull
        }

        else -> {
            null
        }
    }
}

private class MutableTextProjection(
    delegate: TextProjectionProtocol,
    storage: StorageProjectionProtocol<TextTypeAlias>
) : TextProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

private class ContextualTextProjection(
    private val delegate: TextProjectionProtocol,
    override val type: String
) : TextProjectionProtocol by delegate,
    ContextualUpdaterProcessorProtocol

val TextProjectionProtocol.mutable
    get(): TextProjectionProtocol = MutableTextProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

val TextProjectionProtocol.contextual
    get(): TextProjectionProtocol = ContextualTextProjection(
        delegate = this,
        type = TypeSchema.Value.text
    )

fun createTextProjection(
    key: String
): TextProjectionProtocol = TextProjection(
    idProcessor = IdProcessor(),
    projection = Projection(key = key),
    statusProcessor = ResolveStatusProcessor()
)

fun TypeProjectorProtocols.text(
    key: String,
): TextProjectionProtocol = createTextProjection(key)
