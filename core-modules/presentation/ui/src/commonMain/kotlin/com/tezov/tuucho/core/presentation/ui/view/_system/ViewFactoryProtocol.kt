@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.view._system

import com.tezov.tuucho.core.presentation.ui.screen.ScreenContextProtocol

internal interface ViewFactoryProtocol : ViewFactoryMatcherProtocol {
    suspend fun process(
        screenContext: ScreenContextProtocol,
    ): ViewProtocol
}
