package com.tezov.tuucho.core.domain.business.di

sealed class ModuleContextDomain : KoinMass.ModuleContext {
    object Main : ModuleContextDomain()

    object UseCase : ModuleContextDomain()

    object Middleware : ModuleContextDomain()
}
