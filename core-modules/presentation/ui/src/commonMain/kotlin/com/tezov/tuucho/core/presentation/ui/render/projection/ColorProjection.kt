package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui._system.toColorOrNull
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

interface ColorProjectionProtocol : ProjectionProtocols<Color>

class ColorProjection(
    key: String,
    storage: ProjectionStorageProtocol<Color>,
) : ColorProjectionProtocol,
    ProjectionProtocols<Color> by Projection(
        key = key,
        storage = storage,
        getValueOrNull = { jsonElement ->
            when (jsonElement) {
                is JsonObject -> {
                    jsonElement
                        .withScope(DimensionSchema::Scope)
                        .default
                        ?.toColorOrNull()
                }

                is JsonPrimitive -> {
                    jsonElement.stringOrNull?.toColorOrNull()
                }

                else -> {
                    null
                }
            }
        }
    )

private class ContextualColorProjection(
    private val delegate: ColorProjectionProtocol
) : ColorProjectionProtocol by delegate,
    UpdatableProtocol {
    override val type = TypeSchema.Value.color

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

fun createColorProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): ColorProjectionProtocol {
    val projection = when (mutable) {
        true -> ColorProjection(key, Projection.Mutable())
        false -> ColorProjection(key, Projection.Static())
    }
    return when {
        contextual -> ContextualColorProjection(projection)
        else -> projection
    }
}
