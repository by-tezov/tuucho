package com.tezov.tuucho.core.presentation.ui.render.projection.dimension

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projection.ExtractorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.MutableStorageProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.Projection
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.StorageProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias DpTypeAlias = Dp

private typealias DpProjectionTypeAlias = ProjectionProtocols<DpTypeAlias>

interface DpProjectionProtocol :
    IdProcessorProtocol,
    DpProjectionTypeAlias,
    ResolveStatusProcessorProtocol

private class DpProjection(
    private val idProcessor: IdProcessorProtocol,
    private val projection: DpProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : DpProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    DpProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status {
    init {
        attach(this as ExtractorProjectionProtocol<DpTypeAlias>)
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
                ?.dp
        }

        is JsonPrimitive -> {
            jsonElement.stringOrNull?.toFloatOrNull()?.dp
        }

        else -> {
            null
        }
    }
}

private class MutableDpProjection(
    delegate: DpProjectionProtocol,
    storage: StorageProjectionProtocol<DpTypeAlias>
) : DpProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

val DpProjectionProtocol.mutable
    get(): DpProjectionProtocol = MutableDpProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

fun createDpProjection(
    key: String,
): DpProjectionProtocol = DpProjection(
    idProcessor = IdProcessor(),
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
)

fun TypeProjectorProtocols.dp(
    key: String
): DpProjectionProtocol = createDpProjection(key)
