package com.tezov.tuucho.core.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tezov.tuucho.core.data.database.Database
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object DatabaseRepositoryModuleAndroid {

    internal operator fun invoke() = module {

        factory<SqlDriver> {
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = androidContext(),
                name = localDatabaseName
            )
        }
    }

}
