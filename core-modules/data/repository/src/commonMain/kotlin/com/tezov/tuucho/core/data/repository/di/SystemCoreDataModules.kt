package com.tezov.tuucho.core.data.repository.di

import org.koin.core.module.Module

internal expect fun SystemCoreDataModules.platformInvoke():List<Module>

object SystemCoreDataModules {

    fun invoke():List<Module> = listOf(
        MiscModule.invoke(),
        MaterialRectifierModule.invoke(),
        MaterialBreakerModule.invoke(),
        MaterialAssemblerModule.invoke(),
        MaterialShadowerModule.invoke(),
        MaterialRepositoryModule.invoke(),
        DatabaseRepositoryModule.invoke(),
        NetworkRepositoryModule.invoke(),
    ) + platformInvoke()

}
