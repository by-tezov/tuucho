package com.tezov.tuucho.sample.shared.di

internal object NetworkRepositoryModuleIosFlavor {
    fun invoke() = NetworkModuleIos.FlavorDefault.invoke()
}
