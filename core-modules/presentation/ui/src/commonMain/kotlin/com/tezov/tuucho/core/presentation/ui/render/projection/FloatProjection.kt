package com.tezov.tuucho.core.presentation.ui.render.projection

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

private typealias FloatProjectionTypeAlias = ProjectionProtocols<Float>

interface FloatProjectionProtocol : FloatProjectionTypeAlias

class FloatProjection(
    private val projection: FloatProjectionTypeAlias,
) : FloatProjectionProtocol,
    FloatProjectionTypeAlias by projection {
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
    private val delegate: FloatProjectionProtocol,
    private val updatable: UpdatableProtocol,
    private val status: ReadyStatusProtocol
) : FloatProjectionProtocol by delegate,
    UpdatableProtocol by updatable,
    ReadyStatusProtocol by status {

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        delegate.process(jsonElement)
        updatable.process(jsonElement)
        status.update(jsonElement)
    }
}

fun createFloatProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): FloatProjectionProtocol {
    val projection: FloatProjectionTypeAlias = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val floatProjection = FloatProjection(projection)
    return when {
        contextual -> ContextualFloatProjection(
            delegate = floatProjection,
            updatable = Updatable(TypeSchema.Value.dimension),
            status = ReadyStatus()
        )
        else -> floatProjection
    }
}
