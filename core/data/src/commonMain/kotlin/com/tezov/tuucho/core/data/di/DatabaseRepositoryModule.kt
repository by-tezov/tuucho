package com.tezov.tuucho.core.data.di

import app.cash.sqldelight.db.SqlDriver
import com.tezov.tuucho.core.data.database.Database
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
                get<SqlDriver>(),
                get<JsonObjectEntry.Adapter>()
            )
        }

        single<JsonObjectQueries> {
            JsonObjectQueries(
                get<Database>()
            )
        }

        single<VersioningQueries> {
            VersioningQueries(
                get<Database>()
            )
        }
    }

}
