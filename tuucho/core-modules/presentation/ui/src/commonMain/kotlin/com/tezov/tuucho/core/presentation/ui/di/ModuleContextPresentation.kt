package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.di.KoinMass

sealed class ModuleContextPresentation : KoinMass.ModuleContext {
    object Main : ModuleContextPresentation()

    object View : ModuleContextPresentation()
}
