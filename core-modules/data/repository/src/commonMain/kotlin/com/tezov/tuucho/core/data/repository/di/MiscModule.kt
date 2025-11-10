package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.source._system.LifetimeResolver
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module

internal object MiscModule {
    fun invoke() = module(ModuleGroupData.Main) {
        factory<LifetimeResolver> {
            LifetimeResolver(
                expirationDateTimeRectifier = get()
            )
        }
    }
}
