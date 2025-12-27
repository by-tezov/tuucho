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

private typealias ColorProjectionProtocols = ProjectionProtocols<Color>

interface ColorProjectionProtocol : ColorProjectionProtocols

class ColorProjection(
    private val projection: ColorProjectionProtocols,
) : ColorProjectionProtocol,
    ColorProjectionProtocols by projection {
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

private class ContextualColorProjection(
    private val delegate: ColorProjectionProtocol
) : ColorProjectionProtocol by delegate,
    UpdatableProtocol {
    override val type = TypeSchema.Value.color

    override var id: String? = null
        private set

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (id == null) {
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
    val projection: ColorProjectionProtocols = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val colorProjection = ColorProjection(projection)
    return when {
        contextual -> ContextualColorProjection(colorProjection)
        else -> colorProjection
    }
}
