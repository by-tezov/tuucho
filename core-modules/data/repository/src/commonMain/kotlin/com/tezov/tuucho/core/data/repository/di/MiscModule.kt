package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.source._system.LifetimeResolver
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module

internal object MiscModule {
    fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            factory<LifetimeResolver> {
                LifetimeResolver(
                    expirationDateTimeRectifier = get()
                )
            }
        }
    }
}
