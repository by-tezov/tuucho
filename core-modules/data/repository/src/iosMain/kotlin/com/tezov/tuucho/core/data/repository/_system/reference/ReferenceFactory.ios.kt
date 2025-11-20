@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system.reference

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import kotlin.reflect.KClass

class ReferenceFactoryIos : ReferenceProtocol.Factory {
    override fun <T : Any> create(
        type: KClass<ReferenceProtocol<*>>,
        value: T?
    ) = when (type) {
        is ReferenceProtocol.Weak<*> -> WeakReferenceIos(value)
        is ReferenceProtocol.Strong<*> -> StrongReferenceIos(value)
        else -> throw DataException.Default("incorrect reference type")
    }
}
