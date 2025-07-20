package com.tezov.tuucho.core.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.converter.JsonObjectConverter
import com.tezov.tuucho.core.data.database.table.JsonObjectEntry
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object DatabaseRepositoryModule {

    private const val databaseName = "database.db"

    internal operator fun invoke() = module {

        factory<SqlDriver> {
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = androidContext(),
                name = databaseName
            )
        }

        factory<JsonObjectEntry.Adapter> {
            JsonObjectEntry.Adapter(
                jsonObjectAdapter = JsonObjectConverter()
            )
        }

//        factory<VersioningEntry.Adapter> {
//            VersioningEntry.Adapter(
//                sharedAdapter = BooleanConverter()
//            )
//        }

        single<Database> {
            //TODO remove and use migration when done
            val dbFile = androidContext().getDatabasePath(databaseName)
            if (dbFile.exists()) {
                dbFile.delete()
            }

            Database(
                get<SqlDriver>(),
                get<JsonObjectEntry.Adapter>()
            )
        }
    }

}
