package com.tezov.tuucho.core.data.di

actual fun SystemCoreDataModules.platformInvoke() = listOf(
    DatabaseRepositoryModuleIos.invoke(),
    MaterialRepositoryModuleIos.invoke()
)