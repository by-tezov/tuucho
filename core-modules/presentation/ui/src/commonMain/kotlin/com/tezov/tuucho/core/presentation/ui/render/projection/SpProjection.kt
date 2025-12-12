package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import kotlinx.serialization.json.JsonElement

object SpProjection {

    private fun getValue(
        jsonElement: JsonElement?
    ) = jsonElement
        ?.withScope(DimensionSchema::Scope)
        ?.default
        ?.toFloatOrNull()
        ?.sp

    class Static(
        key: String
    ) : Projection.AbstractStatic<TextUnit>(key) {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = SpProjection.getValue(jsonElement)
    }

    class Mutable(
        key: String
    ) : Projection.AbstractMutable<TextUnit>(key) {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = SpProjection.getValue(jsonElement)
    }
}
