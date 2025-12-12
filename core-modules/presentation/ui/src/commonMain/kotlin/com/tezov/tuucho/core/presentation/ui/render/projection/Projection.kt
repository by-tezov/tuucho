package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.tezov.tuucho.core.presentation.ui._system.idSourceOrNull
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import kotlinx.serialization.json.JsonElement

sealed class Projection<T : Any>(
    override val key: String,
) : ProjectionProtocol,
    State<T?> {
    override var isReady: Boolean? = null
        protected set

    protected open fun updateIsReady(
        jsonElement: JsonElement?
    ) {
        isReady = jsonElement != null && jsonElement.idSourceOrNull == null
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        updateIsReady(jsonElement)
    }

    abstract suspend fun getValue(
        jsonElement: JsonElement?
    ): T?

    abstract class AbstractStatic<T : Any>(
        key: String
    ) : Projection<T>(key) {
        override var value: T? = null

        override suspend fun process(
            jsonElement: JsonElement?
        ) {
            super.process(jsonElement)
            value = getValue(jsonElement)
        }
    }

    abstract class AbstractMutable<T : Any>(
        key: String,
    ) : Projection<T>(key),
        MutableState<T?> {
        private val _value = mutableStateOf<T?>(null)

        override var value: T?
            get() = _value.value
            set(value) {
                _value.value = value
            }

        override fun component1() = _value.component1()

        override fun component2() = _value.component2()

        override suspend fun process(
            jsonElement: JsonElement?
        ) {
            super.process(jsonElement)
            _value.value = getValue(jsonElement)
        }
    }
}
