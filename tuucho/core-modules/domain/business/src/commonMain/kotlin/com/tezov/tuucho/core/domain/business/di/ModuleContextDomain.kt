package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass

sealed class ModuleContextDomain : KoinMass.ModuleContext {
    object Main : ModuleContextDomain()

    object UseCase : ModuleContextDomain()

    object Middleware : ModuleContextDomain()
}
