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

interface BooleanProjectionProtocol : ProjectionProtocols<Boolean>

class BooleanProjection(
    key: String,
    storage: ProjectionStorageProtocol<Boolean>,
) : BooleanProjectionProtocol,
    ProjectionProtocols<Boolean> by Projection(
        key = key,
        storage = storage,
        getValueOrNull = { jsonElement ->
            when (jsonElement) {
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
    )

private class ContextualBooleanProjection(
    private val delegate: BooleanProjectionProtocol
) : BooleanProjectionProtocol by delegate,
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

fun createBooleanProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): BooleanProjectionProtocol {
    val projection = when (mutable) {
        true -> BooleanProjection(key, Projection.Mutable())
        false -> BooleanProjection(key, Projection.Static())
    }
    return when {
        contextual -> ContextualBooleanProjection(projection)
        else -> projection
    }
}
