package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.repository.RefreshMaterialCacheRepository
import com.tezov.tuucho.core.data.repository.RetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.SendDataAndRetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.ShadowerMaterialRepository
import com.tezov.tuucho.core.data.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.source.MaterialRemoteSource
import com.tezov.tuucho.core.data.source.RetrieveObjectRemoteSource
import com.tezov.tuucho.core.data.source.SendDataAndRetrieveMaterialRemoteSource
import com.tezov.tuucho.core.data.source.shadower.RetrieveOnDemandDefinitionShadowerMaterialSource
import com.tezov.tuucho.core.data.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
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

        factory<MaterialRepositoryProtocol.RefreshCache> {
            RefreshMaterialCacheRepository(
                coroutineScopes = get(),
                retrieveObjectRemoteSource = get(),
                materialRemoteSource = get(),
                materialCacheLocalSource = get()
            )
        }

        factory<MaterialRepositoryProtocol.Retrieve> {
            RetrieveMaterialRepository(
                materialCacheLocalSource = get(),
                materialRemoteSource = get()
            )
        }

        factory<MaterialRepositoryProtocol.SendDataAndRetrieve> {
            SendDataAndRetrieveMaterialRepository(
                sendObjectAndRetrieveMaterialRemoteSource = get()
            )
        }

        single<ShadowerMaterialRepository> {
            ShadowerMaterialRepository(
                coroutineScopes = get(),
                materialShadower = get(),
                shadowerMaterialSources = get<List<ShadowerMaterialSourceProtocol>>(Name.SHADOWER_SOURCE)
            )
        } bind MaterialRepositoryProtocol.Shadower::class
    }

    private fun Module.localSource() {

        factory<MaterialCacheLocalSource> {
            MaterialCacheLocalSource(
                coroutineScopes = get(),
                materialDatabaseSource = get(),
                materialBreaker = get(),
                materialAssembler = get(),
                expirationDateTimeRectifier = get(),
            )
        }

    }

    private fun Module.remoteSource() {

        factory<MaterialRemoteSource> {
            MaterialRemoteSource(
                coroutineScopes = get(),
                materialNetworkSource = get(),
                materialRectifier = get()
            )
        }

        factory<RetrieveObjectRemoteSource> {
            RetrieveObjectRemoteSource(
                coroutineScopes = get(),
                materialNetworkSource = get()
            )
        }

        factory<SendDataAndRetrieveMaterialRemoteSource> {
            SendDataAndRetrieveMaterialRemoteSource(
                coroutineScopes = get(),
                materialNetworkSource = get(),
                materialRectifier = get()
            )
        }
    }

    private fun Module.compositeSource() {
        factory<List<ShadowerMaterialSourceProtocol>>(Name.SHADOWER_SOURCE) {
            listOf(
                RetrieveOnDemandDefinitionShadowerMaterialSource(
                    coroutineScopes = get(),
                    materialNetworkSource = get(),
                    materialRectifier = get(),
                    materialCacheLocalSource = get(),
                    materialAssembler = get(),
                    materialDatabaseSource = get(),
                )
            )
        }
    }

}
