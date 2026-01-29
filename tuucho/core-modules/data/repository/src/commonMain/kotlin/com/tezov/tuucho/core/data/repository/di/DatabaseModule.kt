package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.data.repository.database.DatabaseTransactionFactory
import com.tezov.tuucho.core.data.repository.database.ImageDatabaseSource
import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.dao.HookQueries
import com.tezov.tuucho.core.data.repository.database.dao.ImageQueries
import com.tezov.tuucho.core.data.repository.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.repository.database.table.HookEntry
import com.tezov.tuucho.core.data.repository.database.table.JsonObjectCommonEntry
import com.tezov.tuucho.core.data.repository.database.table.JsonObjectContextualEntry
import com.tezov.tuucho.core.data.repository.database.type.adapter.JsonLifetimeAdapter
import com.tezov.tuucho.core.data.repository.database.type.adapter.JsonObjectAdapter
import com.tezov.tuucho.core.data.repository.database.type.adapter.JsonVisibilityAdapter
import com.tezov.tuucho.core.data.repository.di.DatabaseModule.Name.DATABASE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named

object DatabaseModule {
    interface Config {
        val fileName: String
    }

    object Name {
        val DATABASE_REPOSITORY_CONFIG get() = named("DatabaseRepositoryModule.Name.DATABASE_REPOSITORY_CONFIG")
    }

    internal fun invoke() = module(ModuleContextData.Main) {
        factory<Config>(DATABASE_REPOSITORY_CONFIG) {
            getOrNull<Config>() ?: object : Config {
                override val fileName = "tuucho.database"
            }
        }

        factoryOf(::JsonObjectAdapter)
        factoryOf(::JsonVisibilityAdapter)
        factoryOf(::JsonLifetimeAdapter)

        single {
            Database(
                driver = get(),
                jsonObjectCommonEntryAdapter = JsonObjectCommonEntry.Adapter(
                    jsonObjectAdapter = get<JsonObjectAdapter>()
                ),
                jsonObjectContextualEntryAdapter = JsonObjectContextualEntry.Adapter(
                    jsonObjectAdapter = get<JsonObjectAdapter>()
                ),
                hookEntryAdapter = HookEntry.Adapter(
                    visibilityAdapter = get<JsonVisibilityAdapter>(),
                    lifetimeAdapter = get<JsonLifetimeAdapter>()
                )
            )
        }

        factoryOf(::JsonObjectQueries)
        factoryOf(::HookQueries)
        factoryOf(::ImageQueries)
        factoryOf(::DatabaseTransactionFactory)
        factoryOf(::MaterialDatabaseSource)
        factoryOf(::ImageDatabaseSource)
    }
}
