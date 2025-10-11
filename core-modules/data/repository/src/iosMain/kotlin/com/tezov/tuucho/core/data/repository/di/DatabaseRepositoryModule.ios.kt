package com.tezov.tuucho.core.data.repository.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.tezov.tuucho.core.data.repository.database.Database
import org.koin.dsl.module

internal object DatabaseRepositoryModuleIos {

    fun invoke() = module {

        factory<SqlDriver> {
            NativeSqliteDriver(
                schema = Database.Schema,
                name = get<DatabaseRepositoryModule.Config>().localDatabaseFile
            )
        }
    }

}
