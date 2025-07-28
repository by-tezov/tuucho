package com.tezov.tuucho.core.data.di

import app.cash.sqldelight.db.SqlDriver
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
                JsonObjectConverter()
            )
        }

        single<Database> {
            Database(
                get<SqlDriver>(),
                get<JsonObjectEntry.Adapter>()
            )
        }

        factory<JsonObjectQueries> {
            JsonObjectQueries(
                get<Database>()
            )
        }

        factory<VersioningQueries> {
            VersioningQueries(
                get<Database>()
            )
        }

        single<MaterialDatabaseSource> {
            MaterialDatabaseSource(
                get<VersioningQueries>(),
                get<JsonObjectQueries>()
            )
        }

    }

}
