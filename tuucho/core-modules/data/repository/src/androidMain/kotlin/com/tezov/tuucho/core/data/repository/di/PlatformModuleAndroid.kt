package com.tezov.tuucho.core.data.repository.di

import android.content.Context
import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileAndroid
import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileProtocol
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationAndroid
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationProtocol
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactoryAndroid
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

object PlatformModuleAndroid {
    object Name {
        val APPLICATION_CONTEXT get() = named("PlatformModuleAndroid.Name.APPLICATION_CONTEXT")
    }

    internal fun invoke() = module(ModuleContextData.Main) {
        factory<ReferenceFactoryAndroid>() bind ReferenceProtocol.Factory::class

        factory<SystemPlatformInformationAndroid>() bind SystemPlatformInformationProtocol::class

        factory<SystemPlatformFileProtocol> {
            SystemPlatformFileAndroid(
                context = get<Context>(Name.APPLICATION_CONTEXT)
            )
        }
    }
}
