package com.tezov.tuucho.core.data.di

import org.koin.dsl.module

actual fun serverUrlEndpoint() = "http://10.0.2.2:3000"

object NetworkRepositoryModuleAndroid {

    internal operator fun invoke() = module {
        NetworkRepositoryModuleAndroidFlavor.invoke(this)
    }

}