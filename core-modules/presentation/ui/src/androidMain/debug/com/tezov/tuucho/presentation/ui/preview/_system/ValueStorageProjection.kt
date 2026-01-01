@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.presentation.ui.preview._system

import com.tezov.tuucho.core.presentation.ui.render.projection.ValueStorageProjectionProtocol

fun <T : Any> storageOf(
    value: T?
) = object : ValueStorageProjectionProtocol<T> {
    override var value: T? = value
}
