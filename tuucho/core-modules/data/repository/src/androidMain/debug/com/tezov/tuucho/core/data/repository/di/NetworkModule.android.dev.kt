package com.tezov.tuucho.core.data.repository.di

internal object NetworkModuleAndroidFlavor {
    fun invoke() = NetworkModuleAndroid.FlavorDefault
        .invoke()
}
