package com.tezov.tuucho.core.presentation.ui.render.projection.dimension

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projection.ExtractorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.MutableStorageProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.Projection
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.StorageProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias StringTypeAlias = String

private typealias StringProjectionTypeAlias = ProjectionProtocols<StringTypeAlias>

interface StringProjectionProtocol :
    IdProcessorProtocol,
    ResolveStatusProcessorProtocol,
    StringProjectionTypeAlias

private class StringProjection(
    private val idProcessor: IdProcessorProtocol,
    private val projection: StringProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : StringProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    StringProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status {
    init {
        attach(this as ExtractorProjectionProtocol<StringTypeAlias>)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        idProcessor.process(jsonElement)
        projection.process(jsonElement)
        status.update(jsonElement)
    }

    override suspend fun extract(
        jsonElement: JsonElement?
    ) = when (jsonElement) {
        is JsonObject -> {
            jsonElement
                .withScope(DimensionSchema::Scope)
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

private class MutableStringProjection(
    delegate: StringProjectionProtocol,
    storage: StorageProjectionProtocol<StringTypeAlias>
) : StringProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

private class ContextualStringProjection(
    private val delegate: StringProjectionProtocol,
    override val type: String
) : StringProjectionProtocol by delegate,
    ContextualUpdaterProcessorProtocol

val StringProjectionProtocol.mutable
    get(): StringProjectionProtocol = MutableStringProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

val StringProjectionProtocol.contextual
    get(): StringProjectionProtocol = ContextualStringProjection(
        delegate = this,
        type = TypeSchema.Value.dimension
    )

fun createStringProjection(
    key: String
): StringProjectionProtocol = StringProjection(
    idProcessor = IdProcessor(),
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
)

fun TypeProjectorProtocols.string(
    key: String
): StringProjectionProtocol = createStringProjection(key)
