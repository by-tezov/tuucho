package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonElement

object StringProjection {
    private fun getValue(
        jsonElement: JsonElement?
    ) = jsonElement.stringOrNull

    class Static(
        key: String
    ) : Projection.AbstractStatic<String>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = StringProjection.getValue(jsonElement)
    }

    class Mutable(
        key: String
    ) : Projection.AbstractMutable<String>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = StringProjection.getValue(jsonElement)
    }
}
