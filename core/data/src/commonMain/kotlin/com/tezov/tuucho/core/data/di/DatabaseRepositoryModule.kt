package com.tezov.tuucho.core.data.di

import app.cash.sqldelight.db.SqlDriver
import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.converter.JsonObjectConverter
import com.tezov.tuucho.core.data.database.table.JsonObjectEntry
import org.koin.core.qualifier.named
import org.koin.dsl.module

object DatabaseRepositoryModule {

    const val databaseName = "database.db" //TODO

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
    }

}
