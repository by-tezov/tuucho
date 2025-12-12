package com.tezov.tuucho.core.presentation.ui.render.projection

import kotlinx.serialization.json.JsonElement

object TextMessageProjection {
    class Static(
        key: String
    ) : Projection.AbstractStatic<String>(key) {

        lateinit var onReceived: ((String?) -> Unit)

        private val textProjection = TextProjection.Static(key)

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = textProjection.getValue(jsonElement)

        override suspend fun process(
            jsonElement: JsonElement?
        ) {
            super.process(jsonElement)
            onReceived.invoke(value)
        }
    }

    class Mutable(
        key: String
    ) : Projection.AbstractMutable<String>(key) {

        lateinit var onReceived: ((String?) -> Unit)

        private val textProjection = TextProjection.Mutable(key)

        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = textProjection.getValue(jsonElement)

        override suspend fun process(
            jsonElement: JsonElement?
        ) {
            super.process(jsonElement)
            onReceived.invoke(value)
        }
    }
}
