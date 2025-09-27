package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.data.repository.database.DatabaseTransactionFactory
import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.dao.HookQueries
import com.tezov.tuucho.core.data.repository.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.repository.database.table.HookEntry
import com.tezov.tuucho.core.data.repository.database.table.JsonObjectCommonEntry
import com.tezov.tuucho.core.data.repository.database.table.JsonObjectContextualEntry
import com.tezov.tuucho.core.data.repository.database.type.adapter.JsonObjectAdapter
import com.tezov.tuucho.core.data.repository.database.type.adapter.LifetimeAdapter
import com.tezov.tuucho.core.data.repository.database.type.adapter.VisibilityAdapter
import org.koin.dsl.module

internal object DatabaseRepositoryModule {

    fun invoke() = module {

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

        factory<MaterialDatabaseSource> {
            MaterialDatabaseSource(
                transactionFactory = get(),
                hookQueries = get(),
                jsonObjectQueries = get()
            )
        }
    }

}
