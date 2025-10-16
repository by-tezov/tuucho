package com.tezov.tuucho.shared.sample.di

actual fun SystemSharedModules.platformInvoke() = listOf(
    ConfigModuleIos.invoke(),
    NetworkModuleIos.invoke(),
)