package com.tezov.tuucho.core.presentation.ui.render.projection.dimension

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
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

private typealias SpTypeAlias = TextUnit

private typealias SpProjectionTypeAlias = ProjectionProtocols<TextUnit>

interface SpProjectionProtocol :
    IdProcessorProtocol,
    SpProjectionTypeAlias,
    ResolveStatusProcessorProtocol

private class SpProjection(
    private val idProcessor: IdProcessorProtocol,
    private val projection: SpProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : SpProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    SpProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status {
    init {
        attach(this as ExtractorProjectionProtocol<SpTypeAlias>)
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
                ?.toFloatOrNull()
                ?.sp
        }

        is JsonPrimitive -> {
            jsonElement.stringOrNull?.toFloatOrNull()?.sp
        }

        else -> {
            null
        }
    }
}

private class MutableSpProjection(
    delegate: SpProjectionProtocol,
    storage: StorageProjectionProtocol<SpTypeAlias>
) : SpProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

private class ContextualSpProjection(
    private val delegate: SpProjectionProtocol,
    override val type: String
) : SpProjectionProtocol by delegate,
    ContextualUpdaterProcessorProtocol

val SpProjectionProtocol.mutable
    get(): SpProjectionProtocol = MutableSpProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

val SpProjectionProtocol.contextual
    get(): SpProjectionProtocol = ContextualSpProjection(
        delegate = this,
        type = TypeSchema.Value.dimension
    )

fun createSpProjection(
    key: String
): SpProjectionProtocol = SpProjection(
    idProcessor = IdProcessor(),
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
)

fun TypeProjectorProtocols.sp(
    key: String
): SpProjectionProtocol = createSpProjection(key)
