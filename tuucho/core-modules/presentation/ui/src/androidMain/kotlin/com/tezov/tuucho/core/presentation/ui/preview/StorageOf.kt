package com.tezov.tuucho.core.presentation.ui.preview

import com.tezov.tuucho.core.presentation.ui.render.projection.ValueStorageProjectionProtocol

object StorageOf {
    operator fun <T : Any> invoke(
        value: T?
    ) = object : ValueStorageProjectionProtocol<T> {
        override var value: T? = value
    }
}
