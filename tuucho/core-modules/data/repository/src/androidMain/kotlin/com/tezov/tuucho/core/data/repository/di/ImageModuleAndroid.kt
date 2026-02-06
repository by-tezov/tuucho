package com.tezov.tuucho.core.data.repository.di

import coil3.PlatformContext
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module

internal object ImageModuleAndroid {
    fun invoke() = module(ModuleContextData.Main) {
        factory<PlatformContext> {
            get(PlatformModuleAndroid.Name.APPLICATION_CONTEXT) // coil
        }
    }
}
