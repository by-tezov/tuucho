package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.DatabaseTransactionFactory
import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.database.MaterialDatabaseSourceProtocol
import com.tezov.tuucho.core.data.database.dao.HookQueries
import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.table.HookEntry
import com.tezov.tuucho.core.data.database.table.JsonObjectCommonEntry
import com.tezov.tuucho.core.data.database.table.JsonObjectContextualEntry
import com.tezov.tuucho.core.data.database.type.adapter.JsonObjectAdapter
import com.tezov.tuucho.core.data.database.type.adapter.LifetimeAdapter
import com.tezov.tuucho.core.data.database.type.adapter.VisibilityAdapter
import org.koin.dsl.module

const val localDatabaseName = "database.db" //TODO external config file (android, ios, common)

object DatabaseRepositoryModule {

    internal operator fun invoke() = module {

        factory<JsonObjectAdapter> {
            JsonObjectAdapter(json = get())
        }

        factory<LifetimeAdapter> {
            LifetimeAdapter(json = get())
        }

        factory<VisibilityAdapter> {
            VisibilityAdapter(json = get())
        }

        single<Database> {
            Database(
                driver = get(),
                jsonObjectCommonEntryAdapter = JsonObjectCommonEntry.Adapter(
                    jsonObjectAdapter = get<JsonObjectAdapter>()
                ),
                jsonObjectContextualEntryAdapter = JsonObjectContextualEntry.Adapter(
                    jsonObjectAdapter = get<JsonObjectAdapter>()
                ),
                hookEntryAdapter = HookEntry.Adapter(
                    visibilityAdapter = get<VisibilityAdapter>(),
                    lifetimeAdapter = get<LifetimeAdapter>()
                ),
            )
        }

        factory<JsonObjectQueries> {
            JsonObjectQueries(
                database = get()
            )
        }

        factory<HookQueries> {
            HookQueries(
                database = get()
            )
        }

        factory<DatabaseTransactionFactory> {
            DatabaseTransactionFactory(
                database = get()
            )
        }

        factory<MaterialDatabaseSourceProtocol> {
            MaterialDatabaseSource(
                transactionFactory = get(),
                hookQueries = get(),
                jsonObjectQueries = get()
            )
        }
    }

}
