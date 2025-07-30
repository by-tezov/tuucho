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
                materialShadower = get()
            )
        } bind ShadowerMaterialRepositoryProtocol::class

        single<RetrieveMaterialRepositoryProtocol> {
            RetrieveMaterialRepository(
                retrieveMaterialCacheLocalSource = get(),
                retrieveMaterialRemoteSource = get(),
                refreshMaterialCacheLocalSource = get(),
                shadowerMaterialRepository = get(),
            )
        }

        single<RefreshCacheMaterialRepositoryProtocol> {
            RefreshCacheMaterialRepository(
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
                materialDatabaseSource = get(),
                materialBreaker = get()
            )
        }

        factory<RetrieveMaterialCacheLocalSource> {
            RetrieveMaterialCacheLocalSource(
                materialDatabaseSource = get(),
                materialAssembler = get()
            )
        }
    }

    private fun Module.remoteSource() {

        factory<RetrieveMaterialRemoteSource> {
            RetrieveMaterialRemoteSource(
                materialNetworkSource = get(),
                materialRectifier = get()
            )
        }

        factory<RetrieveObjectRemoteSource> {
            RetrieveObjectRemoteSource(
                materialNetworkSource = get()
            )
        }

        factory<SendDataAndRetrieveMaterialRemoteSource> {
            SendDataAndRetrieveMaterialRemoteSource(
                materialNetworkSource = get(),
                materialRectifier = get()
            )
        }
    }

    private fun Module.compositeSource() {
        factory<List<ShadowerMaterialSourceProtocol>>(Name.SHADOWER_SOURCE) {
            listOf(
                RetrieveOnDemandDefinitionShadowerMaterialSource(
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
