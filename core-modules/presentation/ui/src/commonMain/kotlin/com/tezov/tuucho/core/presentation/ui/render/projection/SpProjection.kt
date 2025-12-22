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

interface SpProjectionProtocol : ProjectionProtocols<TextUnit>

class SpProjection(
    key: String,
    storage: ProjectionStorageProtocol<TextUnit>,
) : SpProjectionProtocol,
    ProjectionProtocols<TextUnit> by Projection(
        key = key,
        storage = storage,
        getValueOrNull = { jsonElement ->
            when (jsonElement) {
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
    )

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
    val projection = when (mutable) {
        true -> SpProjection(key, Projection.Mutable())
        false -> SpProjection(key, Projection.Static())
    }
    return when {
        contextual -> ContextualSpProjection(projection)
        else -> projection
    }
}
