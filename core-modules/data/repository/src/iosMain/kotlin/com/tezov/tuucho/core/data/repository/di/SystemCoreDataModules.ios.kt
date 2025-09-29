package com.tezov.tuucho.core.data.repository.di

internal actual fun SystemCoreDataModules.platformInvoke() = listOf(
    DatabaseRepositoryModuleIos.invoke(),
    NetworkRepositoryModuleIos.invoke(),
    AssetsModuleIos.invoke()
)