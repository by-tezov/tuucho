package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias DpProjectionProtocols = ProjectionProtocols<Dp>

interface DpProjectionProtocol : DpProjectionProtocols

class DpProjection(
    private val projection: DpProjectionProtocols,
) : DpProjectionProtocol,
    DpProjectionProtocols by projection {
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
    private val delegate: DpProjectionProtocol
) : DpProjectionProtocol by delegate,
    UpdatableProtocol {
    override val type = TypeSchema.Value.dimension

    override lateinit var id: String
        private set

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (!this::id.isInitialized) {
            jsonElement?.idValue?.let { id = it }
        }
        delegate.process(jsonElement)
    }
}

fun createDpProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): DpProjectionProtocol {
    val projection: DpProjectionProtocols = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val dpProjection = DpProjection(projection)
    return when {
        contextual -> ContextualDpProjection(dpProjection)
        else -> dpProjection
    }
}
