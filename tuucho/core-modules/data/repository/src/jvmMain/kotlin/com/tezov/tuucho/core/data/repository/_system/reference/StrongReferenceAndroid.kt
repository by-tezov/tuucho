@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system.reference

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol

class StrongReferenceAndroid<T : Any>(
    private var value: T? = null
) : ReferenceProtocol.Strong<T> {
    override fun getOrThrow() = value ?: throw DataException.Default("value is null")

    override fun getOrNull(): T? = value

    override fun set(
        value: T
    ) {
        this.value = value
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
