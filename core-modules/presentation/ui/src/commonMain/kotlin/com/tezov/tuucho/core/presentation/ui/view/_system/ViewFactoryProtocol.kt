@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.view._system

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui.screen.Screen

internal interface ViewFactoryProtocol : ViewFactoryMatcherProtocol {
    suspend fun process(
        screen: Screen,
        path: JsonElementPath,
    ): ViewProtocol
}
