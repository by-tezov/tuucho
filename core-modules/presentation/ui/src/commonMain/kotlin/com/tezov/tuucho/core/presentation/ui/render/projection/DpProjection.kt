package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui.render.misc.ReadyStatus
import com.tezov.tuucho.core.presentation.ui.render.misc.Updatable
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias DpProjectionTypeAlias = ProjectionProtocols<Dp>

interface DpProjectionProtocol : DpProjectionTypeAlias

class DpProjection(
    private val projection: DpProjectionTypeAlias,
) : DpProjectionProtocol,
    DpProjectionTypeAlias by projection {
    init {
        attach(this)
    }

    override suspend fun getValueOrNull(
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

private class ContextualDpProjection(
    private val delegate: DpProjectionProtocol,
    private val updatable: UpdatableProtocol,
    private val status: ReadyStatusProtocol
) : DpProjectionProtocol by delegate,
    UpdatableProtocol by updatable,
    ReadyStatusProtocol by status  {

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        delegate.process(jsonElement)
        updatable.process(jsonElement)
        status.update(jsonElement)
    }
}

fun createDpProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): DpProjectionProtocol {
    val projection: DpProjectionTypeAlias = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val dpProjection = DpProjection(projection)
    return when {
        contextual -> ContextualDpProjection(
            delegate = dpProjection,
            updatable = Updatable(TypeSchema.Value.dimension),
            status = ReadyStatus()
        )
        else -> dpProjection
    }
}
