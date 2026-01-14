package com.tezov.tuucho.core.presentation.ui.render.projection.dimension

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projection.MutableStorageProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias IntTypeAlias = Int

private typealias IntProjectionTypeAlias = com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols<IntTypeAlias>

interface IntProjectionProtocol :
    IdProcessorProtocol,
    IntProjectionTypeAlias,
    ResolveStatusProcessorProtocol

private class IntProjection(
    private val idProcessor: IdProcessorProtocol,
    private val projection: IntProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : IntProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    IntProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status {
    init {
        attach(this as com.tezov.tuucho.core.presentation.ui.render.projection.ExtractorProjectionProtocol<IntTypeAlias>)
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
                ?.toIntOrNull()
        }

        is JsonPrimitive -> {
            jsonElement.stringOrNull?.toIntOrNull()
        }

        else -> {
            null
        }
    }
}

private class MutableIntProjection(
    delegate: IntProjectionProtocol,
    storage: com.tezov.tuucho.core.presentation.ui.render.projection.StorageProjectionProtocol<IntTypeAlias>
) : IntProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

private class ContextualIntProjection(
    private val delegate: IntProjectionProtocol,
    override val type: String
) : IntProjectionProtocol by delegate,
    ContextualUpdaterProcessorProtocol

val IntProjectionProtocol.mutable
    get(): IntProjectionProtocol = MutableIntProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

val IntProjectionProtocol.contextual
    get(): IntProjectionProtocol = ContextualIntProjection(
        delegate = this,
        type = TypeSchema.Value.dimension
    )

fun createIntProjection(
    key: String
): IntProjectionProtocol = IntProjection(
    idProcessor = IdProcessor(),
    projection = _root_ide_package_.com.tezov.tuucho.core.presentation.ui.render.projection
        .Projection(key = key),
    status = ResolveStatusProcessor()
)

fun TypeProjectorProtocols.int(
    key: String
): IntProjectionProtocol = createIntProjection(key)
