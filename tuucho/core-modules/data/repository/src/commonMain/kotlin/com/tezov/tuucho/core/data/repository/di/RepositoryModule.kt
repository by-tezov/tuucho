package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.ImageRepository
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
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

internal object RepositoryModule {
    object Name {
        val SHADOWER_SOURCE get() = named("MaterialRepositoryModule.Name.SHADOWER_SOURCE")
    }

    fun invoke() = module(ModuleContextData.Main) {
        source()
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

        factory<ImageRepository>() bind ImageRepositoryProtocol::class
        factory<RefreshMaterialCacheRepository>() bind MaterialRepositoryProtocol.RefreshCache::class
        factory<RetrieveMaterialRepository>() bind MaterialRepositoryProtocol.Retrieve::class
        factory<SendDataAndRetrieveMaterialRepository>() bind MaterialRepositoryProtocol.SendDataAndRetrieve::class
        factory<ServerHealthCheckRepository>() bind ServerHealthCheckRepositoryProtocol::class
        single {
            ShadowerMaterialRepository(
                coroutineScopes = get(),
                materialShadower = get(),
                shadowerMaterialSources = get<List<ShadowerMaterialSourceProtocol>>(Name.SHADOWER_SOURCE)
            )
        } bind MaterialRepositoryProtocol.Shadower::class
    }

    private fun Module.source() {
        factory<MaterialCacheLocalSource>()
        factory<MaterialRemoteSource>()
        factory<RemoteSource>()
        factory<SendDataAndRetrieveMaterialRemoteSource>()
        factory<ImageSource>()

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
