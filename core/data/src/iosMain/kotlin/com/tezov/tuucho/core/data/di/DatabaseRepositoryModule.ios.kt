package com.tezov.tuucho.core.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.tezov.tuucho.core.data.database.Database
import org.koin.dsl.module

object DatabaseRepositoryModuleIos {

    internal operator fun invoke() = module {

        factory<SqlDriver> {
            NativeSqliteDriver(
                schema = Database.Schema,
                name = localDatabaseName
            )
        }
    }

}
