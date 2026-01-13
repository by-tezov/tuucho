@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier.material._system

import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol

class RectifierIdGenerator internal constructor(
    private val idGenerator: IdGeneratorProtocol<Unit, String>
) : IdGeneratorProtocol<Unit, String> by idGenerator
