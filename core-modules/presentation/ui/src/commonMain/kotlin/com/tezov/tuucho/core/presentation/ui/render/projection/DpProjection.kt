package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import kotlinx.serialization.json.JsonElement

object DpProjection {

    private fun getValue(
        jsonElement: JsonElement?
    ) = jsonElement
        ?.withScope(DimensionSchema::Scope)
        ?.default
        ?.toFloatOrNull()
        ?.dp

    class Static(
        key: String
    ) : Projection.AbstractStatic<Dp>(key) {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = DpProjection.getValue(jsonElement)
    }

    class Mutable(
        key: String
    ) : Projection.AbstractMutable<Dp>(key) {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = DpProjection.getValue(jsonElement)
    }
}
