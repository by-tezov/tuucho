package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.domain.business.di.KoinMass

sealed class ModuleContextCore : KoinMass.ModuleContext {
    data object Main : ModuleContextCore()
}
