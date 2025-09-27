package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetsAndroid
import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid.Name
import org.koin.dsl.module

internal object AssetsModuleAndroid {

    fun invoke() = module {
        factory<AssetsProtocol> {
            AssetsAndroid(
                context = get(Name.APPLICATION_CONTEXT),
            )
        }
    }

}