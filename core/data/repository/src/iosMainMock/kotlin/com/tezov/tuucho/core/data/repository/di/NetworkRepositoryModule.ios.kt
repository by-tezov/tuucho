package com.tezov.tuucho.core.data.repository.di

import org.koin.core.module.Module

internal object NetworkRepositoryModuleIosFlavor {

    fun invoke() = NetworkRepositoryModuleFlavor.invoke()

}