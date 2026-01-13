package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.RefreshMaterialCacheRepository
import com.tezov.tuucho.core.data.repository.repository.RetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.repository.SendDataAndRetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.repository.ShadowerMaterialRepository
import com.tezov.tuucho.core.data.repository.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.repository.source.MaterialRemoteSource
import com.tezov.tuucho.core.data.repository.repository.source.RetrieveObjectRemoteSource
import com.tezov.tuucho.core.data.repository.repository.source.SendDataAndRetrieveMaterialRemoteSource
import com.tezov.tuucho.core.data.repository.repository.source.shadower.ContextualShadowerMaterialSource
import com.tezov.tuucho.core.data.repository.repository.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal object MaterialRepositoryModule {
    object Name {
        val SHADOWER_SOURCE get() = named("MaterialRepositoryModule.Name.SHADOWER_SOURCE")
    }

    fun invoke() = module(ModuleGroupData.Main) {
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

        factoryOf(::RefreshMaterialCacheRepository) bind MaterialRepositoryProtocol.RefreshCache::class
        factoryOf(::RetrieveMaterialRepository) bind MaterialRepositoryProtocol.Retrieve::class
        factoryOf(::SendDataAndRetrieveMaterialRepository) bind MaterialRepositoryProtocol.SendDataAndRetrieve::class

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
        factoryOf(::MaterialRemoteSource)
        factoryOf(::RetrieveObjectRemoteSource)
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
