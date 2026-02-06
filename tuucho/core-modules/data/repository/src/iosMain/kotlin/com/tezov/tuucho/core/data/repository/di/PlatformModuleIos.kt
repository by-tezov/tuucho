package com.tezov.tuucho.core.data.repository.di

import android.content.Context
import com.tezov.tuucho.core.data.repository._system.SystemPlatformFile
import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileProtocol
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformation
import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationProtocol
import com.tezov.tuucho.core.data.repository._system.reference.ReferenceFactory
import com.tezov.tuucho.core.data.repository.repository.SystemPlatformRepository
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import com.tezov.tuucho.core.domain.tool._system.ReferenceProtocol
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

internal object PlatformModuleIos {
    fun invoke() = module(ModuleContextData.Main) {
        factory<ReferenceFactory>() bind ReferenceProtocol.Factory::class

        factory<SystemPlatformInformation>() bind SystemPlatformInformationProtocol::class

        factory<SystemPlatformFile>() bind SystemPlatformFileProtocol::class
    }
}
