package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.tool.json.booleanOrNull
import kotlinx.serialization.json.JsonElement

object BooleanProjection {
    private fun getValue(
        jsonElement: JsonElement?
    ) = jsonElement.booleanOrNull

    class Static(
        key: String
    ) : Projection.AbstractStatic<Boolean>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = BooleanProjection.getValue(jsonElement)
    }

    class Mutable(
        key: String
    ) : Projection.AbstractMutable<Boolean>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = BooleanProjection.getValue(jsonElement)
    }
}
