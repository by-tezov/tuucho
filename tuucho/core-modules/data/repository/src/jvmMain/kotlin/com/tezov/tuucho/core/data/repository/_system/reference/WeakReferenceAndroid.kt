@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system.reference

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import java.lang.ref.WeakReference

class WeakReferenceAndroid<T : Any>(
    value: T? = null
) : ReferenceProtocol.Weak<T> {
    private var value: WeakReference<T>? = value?.let { WeakReference(value) }

    override fun getOrThrow() = value?.get() ?: throw DataException.Default("value is null")

    override fun getOrNull(): T? = value?.get()

    override fun set(
        value: T
    ) {
        this.value = WeakReference(value)
    }

    override fun clear() {
        value = null
    }

    override fun equals(
        other: Any?
    ): Boolean {
        if (this === other) return true
        if (other !is ReferenceProtocol<*>) return false
        return getOrNull() == other.getOrNull()
    }

    override fun hashCode(): Int = getOrNull()?.hashCode() ?: 0
}
