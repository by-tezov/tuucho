package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

internal actual fun SystemSharedModules.platformInvoke(): List<ModuleProtocol> = listOf(
    NetworkModuleAndroid.invoke(),
    ConfigModuleAndroid.invoke(),
)
