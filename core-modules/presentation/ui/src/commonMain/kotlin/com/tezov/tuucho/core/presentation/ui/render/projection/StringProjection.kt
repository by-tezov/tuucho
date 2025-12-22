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

interface StringProjectionProtocol : ProjectionProtocols<String>

class StringProjection(
    key: String,
    storage: ProjectionStorageProtocol<String>,
) : StringProjectionProtocol,
    ProjectionProtocols<String> by Projection(
        key = key,
        storage = storage,
        getValueOrNull = { jsonElement ->
            when (jsonElement) {
                is JsonObject -> {
                    jsonElement
                        .withScope(DimensionSchema::Scope)
                        .default
                }

                is JsonPrimitive -> {
                    jsonElement.stringOrNull
                }

                else -> {
                    null
                }
            }
        }
    )

private class ContextualStringProjection(
    private val delegate: StringProjectionProtocol
) : StringProjectionProtocol by delegate,
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

fun createStringProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): StringProjectionProtocol {
    val projection = when (mutable) {
        true -> StringProjection(key, Projection.Mutable())
        false -> StringProjection(key, Projection.Static())
    }
    return when {
        contextual -> ContextualStringProjection(projection)
        else -> projection
    }
}
