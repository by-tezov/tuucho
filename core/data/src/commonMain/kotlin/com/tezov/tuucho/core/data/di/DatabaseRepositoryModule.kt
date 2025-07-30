package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.database.converter.JsonObjectConverter
import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.dao.VersioningQueries
import com.tezov.tuucho.core.data.database.table.JsonObjectEntry
import org.koin.dsl.module

const val localDatabaseName = "database.db" //TODO external config file (android, ios, common)

object DatabaseRepositoryModule {

    internal operator fun invoke() = module {

        factory<JsonObjectEntry.Adapter> {
            JsonObjectEntry.Adapter(
                jsonObjectAdapter = JsonObjectConverter()
            )
        }

        single<Database> {
            Database(
                driver = get(),
                jsonObjectEntryAdapter = get()
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

        single<MaterialDatabaseSource> {
            MaterialDatabaseSource(
                versioningQueries = get(),
                jsonObjectQueries = get()
            )
        }

    }

}
