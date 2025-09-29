package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.data.di.ApplicationModules

internal actual fun SystemCoreModules.platformInvoke() = listOf(
    ApplicationModules.invoke()
)