package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileIos
import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileProtocol
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationIos
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationProtocol
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactoryIos
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

internal object PlatformModuleIos {
    fun invoke() = module(ModuleContextData.Main) {
        factory<ReferenceFactoryIos>() bind ReferenceProtocol.Factory::class

        factory<SystemPlatformInformationIos>() bind SystemPlatformInformationProtocol::class

        factory<SystemPlatformFileIos>() bind SystemPlatformFileProtocol::class
    }
}
