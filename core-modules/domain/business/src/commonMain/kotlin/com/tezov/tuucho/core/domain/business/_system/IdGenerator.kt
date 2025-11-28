@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system

import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import kotlin.uuid.Uuid

internal class IdGenerator() : IdGeneratorProtocol<Unit, String> {
    override fun generate() = Uuid.Companion.random().toHexString()
}
