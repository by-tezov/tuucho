package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.data.di.ApplicationModules
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

internal actual fun SystemCoreModules.platformInvoke(): List<ModuleProtocol> = listOf(
    ApplicationModules.invoke()
)