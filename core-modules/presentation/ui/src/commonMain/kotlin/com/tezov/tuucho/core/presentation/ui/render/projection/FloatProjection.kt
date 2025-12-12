package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import kotlinx.serialization.json.JsonElement

object FloatProjection {
    private fun getValue(
        jsonElement: JsonElement?
    ) = jsonElement
        ?.withScope(DimensionSchema::Scope)
        ?.default
        ?.toFloatOrNull()

    class Static(
        key: String
    ) : Projection.AbstractStatic<Float>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = FloatProjection.getValue(jsonElement)
    }

    class Mutable(
        key: String
    ) : Projection.AbstractMutable<Float>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = FloatProjection.getValue(jsonElement)
    }
}
