package com.tezov.tuucho.shared.sample.di

internal object NetworkRepositoryModuleIosFlavor {
    fun invoke() = NetworkModuleIos.FlavorDefault.invoke()
}
