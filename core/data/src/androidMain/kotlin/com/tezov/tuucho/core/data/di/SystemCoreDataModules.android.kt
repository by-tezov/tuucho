package com.tezov.tuucho.core.data.di

actual fun SystemCoreDataModules.platformInvoke() = listOf(
    DatabaseRepositoryModuleAndroid.invoke(),
    NetworkRepositoryModuleAndroid.invoke()
)