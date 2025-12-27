package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias SpProjectionProtocols = ProjectionProtocols<TextUnit>

interface SpProjectionProtocol : SpProjectionProtocols

class SpProjection(
    private val projection: SpProjectionProtocols,
) : SpProjectionProtocol,
    SpProjectionProtocols by projection {
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

private class ContextualSpProjection(
    private val delegate: SpProjectionProtocol
) : SpProjectionProtocol by delegate,
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

fun createSpProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): SpProjectionProtocol {
    val projection: SpProjectionProtocols = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val spProjection = SpProjection(projection)
    return when {
        contextual -> ContextualSpProjection(spProjection)
        else -> spProjection
    }
}
