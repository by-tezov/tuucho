package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

sealed class ModuleGroupPresentation: ModuleProtocol.Group {
    data object Main : ModuleGroupPresentation()
}