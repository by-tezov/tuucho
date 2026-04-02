package com.tezov.tuucho.sample.shared.repository.di

internal object NetworkRepositoryModuleIosFlavor {
    fun invoke() = NetworkModuleIos.FlavorDefault.invoke()
}

