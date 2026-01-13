package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.di.Koin

internal actual fun SystemSharedModules.platformInvoke(): List<Koin> = listOf(
    NetworkModuleAndroid.invoke(),
    ConfigModuleAndroid.invoke(),
)
