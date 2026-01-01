package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui._system.toColorOrNull
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias ColorTypeAlias = Color

private typealias ColorProjectionTypeAlias = ProjectionProtocols<ColorTypeAlias>

interface ColorProjectionProtocol :
    IdProcessorProtocol,
    ColorProjectionTypeAlias,
    ResolveStatusProcessorProtocol

private class ColorProjection(
    private val idProcessor: IdProcessorProtocol,
    private val projection: ColorProjectionTypeAlias,
    private val status: ResolveStatusProcessorProtocol
) : ColorProjectionProtocol,
    IdProcessorProtocol by idProcessor,
    ColorProjectionTypeAlias by projection,
    ResolveStatusProcessorProtocol by status {
    init {
        attach(this as ValueProjectionProtocol<ColorTypeAlias>)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        idProcessor.process(jsonElement)
        projection.process(jsonElement)
        status.update(jsonElement)
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

private class MutableColorProjection(
    delegate: ColorProjectionProtocol,
    storage: StorageProjectionProtocol<ColorTypeAlias>
) : ColorProjectionProtocol by delegate {
    init {
        attach(storage)
    }
}

private class ContextualColorProjection(
    private val delegate: ColorProjectionProtocol,
    override val type: String
) : ColorProjectionProtocol by delegate,
    ContextualUpdaterProcessorProtocol

val ColorProjectionProtocol.mutable
    get(): ColorProjectionProtocol = MutableColorProjection(
        delegate = this,
        storage = MutableStorageProjection()
    )

val ColorProjectionProtocol.contextual
    get(): ColorProjectionProtocol = ContextualColorProjection(
        delegate = this,
        type = TypeSchema.Value.color
    )

fun createColorProjection(
    key: String,
): ColorProjectionProtocol = ColorProjection(
    idProcessor = IdProcessor(),
    projection = Projection(key = key),
    status = ResolveStatusProcessor()
)

fun TypeProjectorProtocols.color(
    key: String
): ColorProjectionProtocol = createColorProjection(key)
