package com.tezov.tuucho.core.data.repository.di

actual fun SystemCoreDataModules.platformInvoke() = listOf(
    DatabaseRepositoryModuleIos.invoke(),
    NetworkRepositoryModuleIos.invoke()
)