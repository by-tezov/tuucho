package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.StoreRepositoryModule.Name.STORE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.qualifier.named

object StoreRepositoryModule {
    interface Config {
        val fileName: String
        val prefixStore: String
    }

    object Name {
        val STORE_REPOSITORY_CONFIG get() = named("StoreRepositoryModule.Name.STORE_REPOSITORY_CONFIG")
    }

    internal fun invoke() = module(ModuleContextData.Main) {
        factory<Config>(STORE_REPOSITORY_CONFIG) {
            getOrNull<Config>() ?: object : Config {
                override val fileName = "tuucho.datastore"
                override val prefixStore = "tuucho.prefix"
            }
        }

    }
}
