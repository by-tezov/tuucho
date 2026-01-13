package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.di.Koin

sealed class ModuleGroupPresentation : Koin.ModuleGroup {
    object Main : ModuleGroupPresentation()

    object View : ModuleGroupPresentation()
}
