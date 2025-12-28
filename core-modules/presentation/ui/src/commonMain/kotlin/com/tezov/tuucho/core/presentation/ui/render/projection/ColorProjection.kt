package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui._system.toColorOrNull
import com.tezov.tuucho.core.presentation.ui.render.misc.ReadyStatus
import com.tezov.tuucho.core.presentation.ui.render.misc.Updatable
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias ColorProjectionTypeAlias = ProjectionProtocols<Color>

interface ColorProjectionProtocol : ColorProjectionTypeAlias

class ColorProjection(
    private val projection: ColorProjectionTypeAlias,
) : ColorProjectionProtocol,
    ColorProjectionTypeAlias by projection {
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
    private val delegate: ColorProjectionProtocol,
    private val updatable: UpdatableProtocol,
    private val status: ReadyStatusProtocol
) : ColorProjectionProtocol by delegate,
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

fun createColorProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): ColorProjectionProtocol {
    val projection: ColorProjectionTypeAlias = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val colorProjection = ColorProjection(projection)
    return when {
        contextual -> ContextualColorProjection(
            delegate = colorProjection,
            updatable = Updatable(TypeSchema.Value.color),
            status = ReadyStatus()
        )
        else -> colorProjection
    }
}
