package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsIos
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import org.koin.dsl.module

object AssetsModuleIos {

    internal operator fun invoke() = module {
        factory<AssetsProtocol> {
            AssetsIos()
        }
    }

}