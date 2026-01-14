package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.KoinMass

internal actual fun SystemSharedModules.platformInvoke(): List<KoinMass> = listOf(
    NetworkModuleAndroid.invoke(),
    ConfigModuleAndroid.invoke(),
)
