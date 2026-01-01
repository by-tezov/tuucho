package com.tezov.tuucho.core.presentation.ui.render.projection.dimension

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projection.MutableStorageProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.Projection
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.StorageProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ValueProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias FloatTypeAlias = Float

private typealias FloatProjectionTypeAlias = ProjectionProtocols<FloatTypeAlias>

interface FloatProjectionProtocol :
    IdProcessorProtocol,
    FloatProjectionTypeAlias,
    ResolveStatusProcessorProtocol

private class FloatProjection(
    private val idProcessor: IdProcessorProtocol,
    private val projection: FloatProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : FloatProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    FloatProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status {
    init {
        attach(this as ValueProjectionProtocol<FloatTypeAlias>)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        idProcessor.process(jsonElement)
        projection.process(jsonElement)
        status.update(jsonElement)
    }

    override suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ) = when (jsonElement) {
        is JsonObject -> {
            jsonElement
                .withScope(DimensionSchema::Scope)
                .default
                ?.toFloatOrNull()
        }

        is JsonPrimitive -> {
            jsonElement.stringOrNull?.toFloatOrNull()
        }

        else -> {
            null
        }
    }
}

private class MutableFloatProjection(
    delegate: FloatProjectionProtocol,
    storage: StorageProjectionProtocol<FloatTypeAlias>
) : FloatProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

private class ContextualFloatProjection(
    private val delegate: FloatProjectionProtocol,
    override val type: String
) : FloatProjectionProtocol by delegate,
    ContextualUpdaterProcessorProtocol

val FloatProjectionProtocol.mutable
    get(): FloatProjectionProtocol = MutableFloatProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

val FloatProjectionProtocol.contextual
    get(): FloatProjectionProtocol = ContextualFloatProjection(
        delegate = this,
        type = TypeSchema.Value.dimension
    )

fun createFloatProjection(
    key: String
): FloatProjectionProtocol = FloatProjection(
    idProcessor = IdProcessor(),
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
)

fun TypeProjectorProtocols.float(
    key: String
): FloatProjectionProtocol = createFloatProjection(key)
