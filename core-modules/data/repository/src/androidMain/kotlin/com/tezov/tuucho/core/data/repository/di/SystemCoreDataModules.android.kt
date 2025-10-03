package com.tezov.tuucho.core.data.repository.di

internal actual fun SystemCoreDataModules.platformInvoke() = listOf(
    DatabaseRepositoryModuleAndroid.invoke(),
    NetworkRepositoryModuleAndroid.invoke(),
    AssetsModuleAndroid.invoke(),
    StoreRepositoryModuleAndroid.invoke()
)