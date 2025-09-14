package com.tezov.tuucho.core.data.repository.parser.rectifier._system

import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import kotlin.uuid.Uuid

class RectifierIdGenerator : IdGeneratorProtocol {

    override fun generate() = Uuid.Companion.random().toHexString()

}