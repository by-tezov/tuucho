package com.tezov.tuucho.core.data.repository.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tezov.tuucho.core.data.repository.database.Database
import org.koin.core.qualifier.named

import org.koin.dsl.module

object DatabaseRepositoryModuleAndroid {

    object Name {
        val APPLICATION_CONTEXT = named("DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT")
    }

    internal operator fun invoke() = module {

        factory<SqlDriver> {
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = get(Name.APPLICATION_CONTEXT),
                name = localDatabaseName
            )
        }
    }

}
