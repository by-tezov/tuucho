package com.tezov.tuucho.core.data.repository.di

internal object NetworkModuleIosFlavor {
    fun invoke() = NetworkModuleIos.FlavorDefault.invoke()
}
