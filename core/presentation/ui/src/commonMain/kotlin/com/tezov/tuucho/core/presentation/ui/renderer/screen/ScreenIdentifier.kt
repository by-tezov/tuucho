package com.tezov.tuucho.core.presentation.ui.renderer.screen

import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.presentation.ui.renderer._system.IdGenerator

class ScreenIdentifier(
    idGenerator: IdGenerator,
) : ScreenProtocol.IdentifierProtocol {

    val value = idGenerator.generate()

    override fun accept(other: SourceIdentifierProtocol): Boolean {
        return other is ScreenIdentifier && other.value == value
    }
}