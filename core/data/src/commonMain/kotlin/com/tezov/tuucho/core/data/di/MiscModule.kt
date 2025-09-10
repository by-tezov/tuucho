package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.source._system.LifetimeResolver
import com.tezov.tuucho.core.data.source._system.LifetimeResolverProtocol
import org.koin.dsl.module

object MiscModule {

    internal operator fun invoke() = module {
        factory<LifetimeResolverProtocol> {
            LifetimeResolver(
                expirationDateTimeRectifier = get()
            )
        }
    }

}


