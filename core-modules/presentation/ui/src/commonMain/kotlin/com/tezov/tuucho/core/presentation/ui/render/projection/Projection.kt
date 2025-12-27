package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.runtime.mutableStateOf
import com.tezov.tuucho.core.presentation.ui._system.idSourceOrNull
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import kotlinx.serialization.json.JsonElement

interface ValueProjectionProtocol<T : Any> {
    fun attach(
        valueProjection: ValueProjectionProtocol<T>
    )

    suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ): T?
}

sealed interface ProjectionStorageProtocol<T> {
    var value: T?
}

interface ProjectionProtocols<T : Any> :
    ProjectionProtocol,
    ProjectionStorageProtocol<T>,
    ValueProjectionProtocol<T> {
    suspend fun superProcess(
        jsonElement: JsonElement?
    )
}

class Projection<T : Any>(
    override val key: String,
    private val storage: ProjectionStorageProtocol<T>
) : ProjectionProtocols<T>,
    ProjectionStorageProtocol<T> by storage {
    class Static<T> : ProjectionStorageProtocol<T> {
        override var value: T? = null

        override fun equals(
            other: Any?
        ) = value == other

        override fun hashCode() = value?.hashCode() ?: 0
    }

    class Mutable<T> : ProjectionStorageProtocol<T> {
        private val state = mutableStateOf<T?>(null)

        override var value: T?
            get() = state.value
            set(v) {
                state.value = v
            }

        override fun equals(
            other: Any?
        ) = state == other

        override fun hashCode() = state.hashCode()
    }

    private lateinit var valueProjection: ValueProjectionProtocol<T>

    override fun attach(
        valueProjection: ValueProjectionProtocol<T>
    ) {
        this.valueProjection = valueProjection
    }

    override var isReady: Boolean? = null
        private set

    fun updateIsReady(
        jsonElement: JsonElement?
    ) {
        isReady = jsonElement != null && jsonElement.idSourceOrNull == null
    }

    override suspend fun superProcess(
        jsonElement: JsonElement?
    ) {
        value = getValueOrNull(jsonElement)
        updateIsReady(jsonElement)
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) = superProcess(jsonElement)

    override suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ) = valueProjection.getValueOrNull(jsonElement)
}
