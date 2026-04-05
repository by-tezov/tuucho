package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.ImageRepository
import com.tezov.tuucho.core.data.repository.repository.RefreshMaterialCacheRepository
import com.tezov.tuucho.core.data.repository.repository.RetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.repository.SendDataAndRetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.repository.ServerHealthCheckRepository
import com.tezov.tuucho.core.data.repository.repository.ShadowerMaterialRepository
import com.tezov.tuucho.core.data.repository.repository.source.ImageSource
import com.tezov.tuucho.core.data.repository.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.repository.source.MaterialConfigRemoteSource
import com.tezov.tuucho.core.data.repository.repository.source.MaterialRemoteSource
import com.tezov.tuucho.core.data.repository.repository.source.RemoteSource
import com.tezov.tuucho.core.data.repository.repository.source.SendDataAndRetrieveMaterialRemoteSource
import com.tezov.tuucho.core.data.repository.repository.source.shadower.ContextualShadowerMaterialSource
import com.tezov.tuucho.core.data.repository.repository.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ServerHealthCheckRepositoryProtocol
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object RepositoryModule {
    fun invoke() = module(ModuleContextData.Main) {
        source()
        repository()
    }

    private fun Module.repository() {
        factory<MaterialRepositoryProtocol.RefreshCache> {
            RefreshMaterialCacheRepository(
                coroutineScopes = get(),
                materialConfigRemoteSource = get(),
                materialRemoteSource = get(),
                materialCacheLocalSource = get()
            )
        }

        factoryOf(::ImageRepository) bind ImageRepositoryProtocol::class
        factoryOf(::RefreshMaterialCacheRepository) bind MaterialRepositoryProtocol.RefreshCache::class
        factoryOf(::RetrieveMaterialRepository) bind MaterialRepositoryProtocol.Retrieve::class
        factoryOf(::SendDataAndRetrieveMaterialRepository) bind MaterialRepositoryProtocol.SendDataAndRetrieve::class
        factoryOf(::ServerHealthCheckRepository) bind ServerHealthCheckRepositoryProtocol::class
        single {
            ShadowerMaterialRepository(
                coroutineScopes = get(),
                materialCacheRepository = get(),
                materialShadower = get(),
                shadowerMaterialSources = getAll<ShadowerMaterialSourceProtocol<ShadowerMaterialSourceProtocol.Context>>()
            )
        } bind MaterialRepositoryProtocol.Shadower::class
    }

    private fun Module.source() {
        factoryOf(::MaterialCacheLocalSource)
        factoryOf(::MaterialConfigRemoteSource)
        factoryOf(::MaterialRemoteSource)
        factoryOf(::RemoteSource)
        factoryOf(::SendDataAndRetrieveMaterialRemoteSource)
        factoryOf(::ImageSource)
        factoryOf(::ContextualShadowerMaterialSource) bind ShadowerMaterialSourceProtocol::class
    }
}
