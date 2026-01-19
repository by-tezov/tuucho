package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.ImageLocalRepository
import com.tezov.tuucho.core.data.repository.repository.ImageRemoteRepository
import com.tezov.tuucho.core.data.repository.repository.RefreshMaterialCacheRepository
import com.tezov.tuucho.core.data.repository.repository.RetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.repository.SendDataAndRetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.repository.ServerHealthCheckRepository
import com.tezov.tuucho.core.data.repository.repository.ShadowerMaterialRepository
import com.tezov.tuucho.core.data.repository.repository.source.ImageSource
import com.tezov.tuucho.core.data.repository.repository.source.MaterialCacheLocalSource
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
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal object RepositoryModule {
    object Name {
        val SHADOWER_SOURCE get() = named("MaterialRepositoryModule.Name.SHADOWER_SOURCE")
    }

    fun invoke() = module(ModuleContextData.Main) {
        localSource()
        remoteSource()
        compositeSource()
        repository()
    }

    private fun Module.repository() {
        factory<MaterialRepositoryProtocol.RefreshCache> {
            RefreshMaterialCacheRepository(
                coroutineScopes = get(),
                remoteSource = get(),
                materialRemoteSource = get(),
                materialCacheLocalSource = get()
            )
        }

        factoryOf(::ImageRemoteRepository) bind ImageRepositoryProtocol.Remote::class
        factoryOf(::ImageLocalRepository) bind ImageRepositoryProtocol.Local::class
        factoryOf(::RefreshMaterialCacheRepository) bind MaterialRepositoryProtocol.RefreshCache::class
        factoryOf(::RetrieveMaterialRepository) bind MaterialRepositoryProtocol.Retrieve::class
        factoryOf(::SendDataAndRetrieveMaterialRepository) bind MaterialRepositoryProtocol.SendDataAndRetrieve::class
        factoryOf(::ServerHealthCheckRepository) bind ServerHealthCheckRepositoryProtocol::class
        single {
            ShadowerMaterialRepository(
                coroutineScopes = get(),
                materialShadower = get(),
                shadowerMaterialSources = get<List<ShadowerMaterialSourceProtocol>>(Name.SHADOWER_SOURCE)
            )
        } bind MaterialRepositoryProtocol.Shadower::class
    }

    private fun Module.localSource() {
        factoryOf(::MaterialCacheLocalSource)
    }

    private fun Module.remoteSource() {
        factoryOf(::ImageSource)
        factoryOf(::MaterialRemoteSource)
        factoryOf(::RemoteSource)
        factoryOf(::SendDataAndRetrieveMaterialRemoteSource)
    }

    private fun Module.compositeSource() {
        factory<List<ShadowerMaterialSourceProtocol>>(Name.SHADOWER_SOURCE) {
            listOf(
                ContextualShadowerMaterialSource(
                    coroutineScopes = get(),
                    materialCacheLocalSource = get(),
                    materialRemoteSource = get(),
                    materialAssembler = get(),
                    materialDatabaseSource = get()
                )
            )
        }
    }
}
