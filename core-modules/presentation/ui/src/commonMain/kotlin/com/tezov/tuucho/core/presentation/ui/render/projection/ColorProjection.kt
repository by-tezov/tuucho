package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ColorSchema
import com.tezov.tuucho.core.presentation.ui._system.toColorOrNull
import kotlinx.serialization.json.JsonElement

object ColorProjection {

    private fun getValue(
        jsonElement: JsonElement?
    ) = jsonElement
        ?.withScope(ColorSchema::Scope)
        ?.default
        ?.toColorOrNull()

    class Static(
        key: String
    ) : Projection.AbstractStatic<Color>(key) {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = ColorProjection.getValue(jsonElement)
    }

    class Mutable(
        key: String
    ) : Projection.AbstractMutable<Color>(key) {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = ColorProjection.getValue(jsonElement)
    }
}
