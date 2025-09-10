package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.source._system.LifetimeResolver
import org.koin.dsl.module

object MiscModule {

    internal operator fun invoke() = module {
        factory<LifetimeResolver> {
            LifetimeResolver(
                expirationDateTimeRectifier = get()
            )
        }
    }

}


