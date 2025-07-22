package com.tezov.tuucho.core.data.di

object SystemCoreDataModules {

    operator fun invoke() = listOf(
        MaterialRectifierModule(),
        MaterialBreakerModule(),
        MaterialAssemblerModule(),
        MaterialRepositoryModule(),
        DatabaseRepositoryModule()
    )

}
