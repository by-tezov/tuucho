package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileIos
import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileProtocol
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationIos
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationProtocol
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactoryIos
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import org.koin.dsl.bind

internal object PlatformModuleIos {
    fun invoke() = module(ModuleContextData.Main) {
        factoryOf(::ReferenceFactoryIos) bind ReferenceProtocol.Factory::class

        factoryOf(::SystemPlatformInformationIos) bind SystemPlatformInformationProtocol::class

        factoryOf(::SystemPlatformFileIos) bind SystemPlatformFileProtocol::class
    }
}
