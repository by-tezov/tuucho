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
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal object MaterialRepositoryModule {

    object Name {
        val SHADOWER_SOURCE = named("MaterialRepositoryModule.Name.SHADOWER_SOURCE")
    }

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
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
                    lifetimeResolver = get(),
                )
            }

        }

        private fun Module.remoteSource() {

            factory<MaterialRemoteSource> {
                MaterialRemoteSource(
                    coroutineScopes = get(),
                    networkJsonObject = get(),
                    materialRectifier = get()
                )
            }

            factory<RetrieveObjectRemoteSource> {
                RetrieveObjectRemoteSource(
                    coroutineScopes = get(),
                    networkJsonObject = get()
                )
            }

            factory<SendDataAndRetrieveMaterialRemoteSource> {
                SendDataAndRetrieveMaterialRemoteSource(
                    coroutineScopes = get(),
                    networkJsonObject = get()
                )
            }
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
}
