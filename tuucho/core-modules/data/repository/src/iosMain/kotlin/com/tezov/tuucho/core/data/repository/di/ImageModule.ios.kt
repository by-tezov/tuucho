package com.tezov.tuucho.core.data.repository.di

import coil3.PlatformContext

internal object ImageModuleIos {
    fun invoke() = module(ModuleContextData.Main) {
        factory<PlatformContext> {
            TODO()
        }
    }
}
