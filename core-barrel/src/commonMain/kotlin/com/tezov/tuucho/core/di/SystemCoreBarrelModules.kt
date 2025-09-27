package com.tezov.tuucho.core.di

import com.tezov.tuucho.core._system.CoroutineScopes
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import org.koin.dsl.module

object SystemCoreBarrelModules {

    interface Config {
        val localDatabaseFile: String
        val serverUrl: String
    }

    fun invoke() = listOf(
        module {

            single<CoroutineScopesProtocol> {
                CoroutineScopes()
            }

            single<SystemCoreDataModules.Config> {
                object : SystemCoreDataModules.Config {
                    override val localDatabaseFile get() = get<Config>().localDatabaseFile
                    override val serverUrl get() = get<Config>().serverUrl
                }
            }
        }
    )
}