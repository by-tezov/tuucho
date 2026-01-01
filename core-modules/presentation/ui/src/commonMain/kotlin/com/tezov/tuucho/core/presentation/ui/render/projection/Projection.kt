package com.tezov.tuucho.core.presentation.ui.render.projection

import androidx.compose.runtime.mutableStateOf
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProcessorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface ValueStorageProjectionProtocol<T : Any> {
    var value: T?
}

interface StorageProjectionProtocol<T : Any> : ValueStorageProjectionProtocol<T> {
    fun attach(
        storageProjection: StorageProjectionProtocol<T>
    )
}

interface ExtractorProjectionProtocol<T : Any> {
    fun attach(
        valueProjection: ExtractorProjectionProtocol<T>
    )

    suspend fun extract(
        jsonElement: JsonElement?
    ): T?
}

interface ProjectionProtocols<T : Any> :
    ProjectionProcessorProtocol,
    StorageProjectionProtocol<T>,
    ExtractorProjectionProtocol<T>

class StaticStorageProjection<T : Any> : StorageProjectionProtocol<T> {
    override var value: T? = null

    override fun attach(
        storageProjection: StorageProjectionProtocol<T>
    ): Unit = throw UiException.Default("not possible")
}

class MutableStorageProjection<T : Any> : StorageProjectionProtocol<T> {
    private val state = mutableStateOf<T?>(null)

    override fun attach(
        storageProjection: StorageProjectionProtocol<T>
    ): Unit = throw UiException.Default("not possible")

    override var value: T?
        get() = state.value
        set(value) {
            state.value = value
        }
}

private class LazyStorageProjection<T : Any> : ReadWriteProperty<Any?, StorageProjectionProtocol<T>> {
    private var initializer: (() -> StorageProjectionProtocol<T>)? = { StaticStorageProjection() }
    private var value: StorageProjectionProtocol<T>? = null

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): StorageProjectionProtocol<T> {
        if (value == null) {
            val init = initializer ?: throw UiException.Default("Initializer was freed but value is null. This should not happen.")
            value = init()
            initializer = null
        }
        return value!!
    }

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: StorageProjectionProtocol<T>
    ) {
        this.value = value
        initializer?.let { initializer = null }
    }
}

class Projection<T : Any>(
    override val key: String,
) : ProjectionProtocols<T>,
    StorageProjectionProtocol<T> {
    private lateinit var valueProjection: ExtractorProjectionProtocol<T>
    private var storageProjection: StorageProjectionProtocol<T> by LazyStorageProjection()

    override fun attach(
        valueProjection: ExtractorProjectionProtocol<T>
    ) {
        this.valueProjection = valueProjection
    }

    override fun attach(
        storageProjection: StorageProjectionProtocol<T>
    ) {
        this.storageProjection = storageProjection
    }

    override var value: T?
        get() = storageProjection.value
        set(value) {
            storageProjection.value = value
        }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        value = extract(jsonElement)
    }

    override suspend fun extract(
        jsonElement: JsonElement?
    ) = valueProjection.extract(jsonElement)
}
