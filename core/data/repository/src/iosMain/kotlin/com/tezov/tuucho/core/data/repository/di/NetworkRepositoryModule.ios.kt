package com.tezov.tuucho.core.data.repository.di

import org.koin.dsl.module

actual fun serverUrlEndpoint() = "http://127.0.0.1:3000"

object NetworkRepositoryModuleIos {

    internal operator fun invoke() = NetworkRepositoryModuleIosFlavor.invoke()

}