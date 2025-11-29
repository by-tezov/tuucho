@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository._system.reference

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import kotlin.reflect.KClass

class ReferenceFactoryAndroid : ReferenceProtocol.Factory {
    override fun <T : Any> create(
        type: KClass<out ReferenceProtocol<*>>,
        value: T?
    ) = when (type) {
        ReferenceProtocol.Weak::class -> _root_ide_package_.com.tezov.tuucho.core.data.repository._system.reference.WeakReferenceAndroid(
            value
        )

        ReferenceProtocol.Strong::class -> _root_ide_package_.com.tezov.tuucho.core.data.repository._system.reference
            .StrongReferenceAndroid(
                value
            )

        else -> throw DataException.Default("incorrect reference type $type")
    }
}
