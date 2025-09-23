package com.tezov.tuucho.core.data.repository.di

import org.koin.core.module.Module

object NetworkRepositoryModuleIosFlavor {

    operator fun invoke(module: Module) = module.apply {
        NetworkRepositoryModuleFlavor.invoke(module)
    }

}