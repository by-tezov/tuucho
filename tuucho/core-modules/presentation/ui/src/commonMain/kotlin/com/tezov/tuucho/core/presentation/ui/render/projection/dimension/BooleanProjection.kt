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

private typealias BooleanTypeAlias = Boolean

private typealias BooleanProjectionTypeAlias = ProjectionProtocols<BooleanTypeAlias>

interface BooleanProjectionProtocol :
    IdProcessorProtocol,
    BooleanProjectionTypeAlias,
    ResolveStatusProcessorProtocol

private class BooleanProjection(
    private val idProcessor: IdProcessorProtocol,
    private val projection: BooleanProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : BooleanProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    BooleanProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status {
    init {
        attach(this as ExtractorProjectionProtocol<BooleanTypeAlias>)
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
                ?.toBooleanStrictOrNull()
        }

        is JsonPrimitive -> {
            jsonElement.stringOrNull
                ?.toBooleanStrictOrNull()
        }

        else -> {
            null
        }
    }
}

private class MutableBooleanProjection(
    delegate: BooleanProjectionProtocol,
    storage: StorageProjectionProtocol<BooleanTypeAlias>
) : BooleanProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

private class ContextualBooleanProjection(
    private val delegate: BooleanProjectionProtocol,
    override val type: String
) : BooleanProjectionProtocol by delegate,
    ContextualUpdaterProcessorProtocol

val BooleanProjectionProtocol.mutable
    get(): BooleanProjectionProtocol = MutableBooleanProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

val BooleanProjectionProtocol.contextual
    get(): BooleanProjectionProtocol = ContextualBooleanProjection(
        delegate = this,
        type = TypeSchema.Value.dimension
    )

fun createBooleanProjection(
    key: String
): BooleanProjectionProtocol = BooleanProjection(
    idProcessor = IdProcessor(),
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
)

fun TypeProjectorProtocols.boolean(
    key: String
): BooleanProjectionProtocol = createBooleanProjection(key)
