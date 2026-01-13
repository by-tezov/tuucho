package com.tezov.tuucho.core.domain.business.di

sealed class ModuleGroupDomain : Koin.ModuleGroup {
    object Main : ModuleGroupDomain()

    object UseCase : ModuleGroupDomain()

    object Middleware : ModuleGroupDomain()
}
