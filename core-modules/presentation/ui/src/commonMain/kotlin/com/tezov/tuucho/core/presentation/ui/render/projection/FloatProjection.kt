package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias FloatProjectionProtocols = ProjectionProtocols<Float>

interface FloatProjectionProtocol : FloatProjectionProtocols

class FloatProjection(
    private val projection: FloatProjectionProtocols,
) : FloatProjectionProtocol,
    FloatProjectionProtocols by projection {
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
        }

        is JsonPrimitive -> {
            jsonElement.stringOrNull?.toFloatOrNull()
        }

        else -> {
            null
        }
    }
}

private class ContextualFloatProjection(
    private val delegate: FloatProjectionProtocol
) : FloatProjectionProtocol by delegate,
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

fun createFloatProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): FloatProjectionProtocol {
    val projection: FloatProjectionProtocols = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val floatProjection = FloatProjection(projection)
    return when {
        contextual -> ContextualFloatProjection(floatProjection)
        else -> floatProjection
    }
}
