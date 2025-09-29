package com.tezov.tuucho.shared.sample.di

internal actual fun SystemSharedModules.platformInvoke() = listOf(
    ConfigModuleAndroid.invoke()
)