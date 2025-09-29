package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.data.di.SystemCoreDataModules
import org.koin.core.module.Module

internal expect fun SystemCoreModules.platformInvoke(): List<Module>

object SystemCoreModules {

    internal fun invoke() = listOf(
        CoroutineScopeModules.invoke(),
        SystemCoreDataModules.invoke()
    ) + platformInvoke()
}