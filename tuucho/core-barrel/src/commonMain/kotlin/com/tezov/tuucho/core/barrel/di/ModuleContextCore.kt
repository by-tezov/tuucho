package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass

sealed class ModuleContextCore : KoinMass.ModuleContext {
    data object Main : ModuleContextCore()
}
