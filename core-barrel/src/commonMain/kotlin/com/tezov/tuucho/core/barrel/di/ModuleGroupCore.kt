package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.domain.business.di.Koin

sealed class ModuleGroupCore : Koin.ModuleGroup {
    data object Main : ModuleGroupCore()
}
