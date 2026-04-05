package com.tezov.tuucho.core.presentation.ui.view.protocol

import com.tezov.tuucho.core.presentation.ui.protocol.ScreenContextProtocol

interface ViewFactoryProtocol : ViewFactoryMatcherProtocol {
    suspend fun process(
        screenContext: ScreenContextProtocol,
    ): ViewProtocol
}
