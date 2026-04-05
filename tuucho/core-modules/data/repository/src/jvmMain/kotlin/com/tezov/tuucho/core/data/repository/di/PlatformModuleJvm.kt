package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileJvm
import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileProtocol
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationJvm
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationProtocol
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactoryJvm
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

object PlatformModuleJvm {
    internal fun invoke() = module(ModuleContextData.Main) {
        factoryOf(::ReferenceFactoryJvm) bind ReferenceProtocol.Factory::class

        factoryOf(::SystemPlatformInformationJvm) bind SystemPlatformInformationProtocol::class

        factoryOf(::SystemPlatformFileJvm) bind SystemPlatformFileProtocol::class
    }
}
