package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.repository.RefreshCacheMaterialRepository
import com.tezov.tuucho.core.data.repository.RetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.SendDataAndRetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.ShadowerMaterialRepository
import com.tezov.tuucho.core.data.source.RefreshMaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.RetrieveMaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.RetrieveMaterialRemoteSource
import com.tezov.tuucho.core.data.source.RetrieveObjectRemoteSource
import com.tezov.tuucho.core.data.source.SendDataAndRetrieveMaterialRemoteSource
import com.tezov.tuucho.core.data.source.shadower.RetrieveOnDemandDefinitionShadowerMaterialSource
import com.tezov.tuucho.core.data.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.SendDataAndRetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ShadowerMaterialRepositoryProtocol
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

object MaterialRepositoryModule {

    object Name {
        val SHADOWER_SOURCE = named("MaterialRepositoryModule.Name.SHADOWER_SOURCE")
    }

    internal operator fun invoke() = module {
        localSource()
        remoteSource()
        compositeSource()
        repository()
    }

    private fun Module.repository() {

        single<ShadowerMaterialRepository> {
            ShadowerMaterialRepository(
                coroutineContextProvider = get(),
                materialShadower = get()
            )
        } bind ShadowerMaterialRepositoryProtocol::class

        single<RetrieveMaterialRepositoryProtocol> {
            RetrieveMaterialRepository(
                coroutineContextProvider = get(),
                retrieveMaterialCacheLocalSource = get(),
                retrieveMaterialRemoteSource = get(),
                refreshMaterialCacheLocalSource = get(),
                shadowerMaterialRepository = get(),
            )
        }

        single<RefreshCacheMaterialRepositoryProtocol> {
            RefreshCacheMaterialRepository(
                coroutineContextProvider = get(),
                retrieveObjectRemoteSource = get(),
                retrieveMaterialRemoteSource = get(),
                refreshMaterialCacheLocalSource = get()
            )
        }

        single<SendDataAndRetrieveMaterialRepositoryProtocol> {
            SendDataAndRetrieveMaterialRepository(
                sendObjectAndRetrieveMaterialRemoteSource = get()
            )
        }
    }

    private fun Module.localSource() {
        factory<RefreshMaterialCacheLocalSource> {
            RefreshMaterialCacheLocalSource(
                coroutineContextProvider = get(),
                materialDatabaseSource = get(),
                materialBreaker = get()
            )
        }

        factory<RetrieveMaterialCacheLocalSource> {
            RetrieveMaterialCacheLocalSource(
                coroutineContextProvider = get(),
                materialDatabaseSource = get(),
                materialAssembler = get()
            )
        }
    }

    private fun Module.remoteSource() {

        factory<RetrieveMaterialRemoteSource> {
            RetrieveMaterialRemoteSource(
                coroutineContextProvider = get(),
                materialNetworkSource = get(),
                materialRectifier = get()
            )
        }

        factory<RetrieveObjectRemoteSource> {
            RetrieveObjectRemoteSource(
                coroutineContextProvider = get(),
                materialNetworkSource = get()
            )
        }

        factory<SendDataAndRetrieveMaterialRemoteSource> {
            SendDataAndRetrieveMaterialRemoteSource(
                coroutineContextProvider = get(),
                materialNetworkSource = get(),
                materialRectifier = get()
            )
        }
    }

    private fun Module.compositeSource() {
        factory<List<ShadowerMaterialSourceProtocol>>(Name.SHADOWER_SOURCE) {
            listOf(
                RetrieveOnDemandDefinitionShadowerMaterialSource(
                    coroutineContextProvider = get(),
                    materialNetworkSource = get(),
                    materialRectifier = get(),
                    refreshMaterialCacheLocalSource = get(),
                    materialAssembler = get(),
                    materialDatabaseSource = get(),
                )
            )
        }

    }

}
