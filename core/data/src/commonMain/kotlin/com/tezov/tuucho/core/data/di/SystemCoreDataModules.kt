package com.tezov.tuucho.core.data.di

import org.koin.core.module.Module

expect fun SystemCoreDataModules.platformInvoke():List<Module>

object SystemCoreDataModules {

    operator fun invoke():List<Module> = listOf(
        MaterialRectifierModule(),
        MaterialBreakerModule(),
        MaterialAssemblerModule(),
        MaterialShadowerModule(),
        MaterialRepositoryModule(),
        DatabaseRepositoryModule(),
        NetworkRepositoryModule(),
    ) + platformInvoke()

}
