package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.DatabaseTransactionFactory
import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.dao.VersioningQueries
import com.tezov.tuucho.core.data.database.table.JsonObjectEntry
import com.tezov.tuucho.core.data.database.table.JsonObjectTransientEntry
import com.tezov.tuucho.core.data.database.table.VersioningEntry
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

        single<Database> {
            Database(
                driver = get(),
                jsonObjectEntryAdapter = JsonObjectEntry.Adapter(
                    jsonObjectAdapter = get<JsonObjectAdapter>()
                ),
                jsonObjectTransientEntryAdapter = JsonObjectTransientEntry.Adapter(
                    jsonObjectAdapter = get<JsonObjectAdapter>()
                ),
                versioningEntryAdapter = VersioningEntry.Adapter(
                    visibilityAdapter = VisibilityAdapter(),
                    lifetimeAdapter = LifetimeAdapter()
                ),
            )
        }

        factory<JsonObjectQueries> {
            JsonObjectQueries(
                database = get()
            )
        }

        factory<VersioningQueries> {
            VersioningQueries(
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
                versioningQueries = get(),
                jsonObjectQueries = get()
            )
        }
    }

}
