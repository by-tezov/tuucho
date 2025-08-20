package com.tezov.tuucho.core.data.di

import org.koin.core.module.Module

object NetworkRepositoryModuleAndroidFlavor {

    operator fun invoke(module: Module) = module.apply {
        NetworkRepositoryModuleFlavor.invoke(module)
    }

}