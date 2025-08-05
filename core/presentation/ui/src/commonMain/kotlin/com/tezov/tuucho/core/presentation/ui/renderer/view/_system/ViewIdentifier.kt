package com.tezov.tuucho.core.presentation.ui.renderer.view._system

import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol
import com.tezov.tuucho.core.presentation.ui.renderer._system.IdGenerator
import com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenIdentifier

class ViewIdentifier(
    idGenerator: IdGenerator,
    val screenIdentifier: ScreenIdentifier,
) : ViewProtocol.IdentifierProtocol {

    val value = idGenerator.generate()

    override fun accept(other: SourceIdentifierProtocol): Boolean {
        return (other is ViewIdentifier && other.value == value) ||
                (other is ScreenIdentifier && other.accept(screenIdentifier))
    }
}