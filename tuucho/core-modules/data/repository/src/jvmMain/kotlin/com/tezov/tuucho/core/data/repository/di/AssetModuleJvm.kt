package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.assets.AssetReaderJvm
import com.tezov.tuucho.core.data.repository.assets.AssetReaderProtocol
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object AssetModuleJvm {
    fun invoke() = module(ModuleContextData.Main) {
        factoryOf(::AssetReaderJvm) bind AssetReaderProtocol::class
    }
}
