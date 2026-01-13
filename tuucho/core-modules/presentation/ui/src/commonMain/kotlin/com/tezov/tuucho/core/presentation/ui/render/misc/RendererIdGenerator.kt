@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.render.misc

import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol

class RendererIdGenerator internal constructor(
    private val idGenerator: IdGeneratorProtocol<Unit, String>
) : IdGeneratorProtocol<Unit, String> by idGenerator
