package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import kotlinx.serialization.json.JsonElement

object TextProjection {

    private fun getValue(
        jsonElement: JsonElement?
    ) = jsonElement
        ?.withScope(TextSchema::Scope)
        ?.default

    class Static(
        key: String
    ) : Projection.AbstractStatic<String>(key) {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = TextProjection.getValue(jsonElement)
    }

    class Mutable(
        key: String
    ) : Projection.AbstractMutable<String>(key) {

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = TextProjection.getValue(jsonElement)
    }
}
