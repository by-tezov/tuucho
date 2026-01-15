package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass

internal actual fun SystemSharedModules.platformInvoke(): List<KoinMass> = listOf(
    NetworkModuleAndroid.invoke(),
    ConfigModuleAndroid.invoke(),
)
