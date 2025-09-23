package com.tezov.tuucho.core.data.repository.di

actual fun SystemCoreDataModules.platformInvoke() = listOf(
    DatabaseRepositoryModuleAndroid.invoke(),
    NetworkRepositoryModuleAndroid.invoke(),
    AssetsModuleAndroid.invoke()
)