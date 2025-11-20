@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.tool._system

import kotlin.reflect.KClass

sealed interface ReferenceProtocol<T : Any> {
    interface Strong<T : Any> : ReferenceProtocol<T>

    interface Weak<T : Any> : ReferenceProtocol<T>

    interface Factory {
        fun <T : Any> create(
            type: KClass<ReferenceProtocol<*>>,
            value: T? = null
        ): ReferenceProtocol<T>
    }

    fun getOrThrow(): T

    fun getOrNull(): T?

    fun set(
        value: T
    )

    fun clear()
}
