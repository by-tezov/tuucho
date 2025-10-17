package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.sample.app.shared.BuildKonfig
import org.koin.dsl.ModuleDeclaration

object NetworkModuleAndroid {

    fun invoke(): ModuleDeclaration = {

        factory<NetworkModule.Config> {
            object : NetworkModule.Config {
                override val headerPlatform = BuildKonfig.headerPlatform
            }
        }
    }

}