package com.tezov.tuucho.core.data.repository.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModule.Name.DATABASE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module

internal object DatabaseModuleIos {
    fun invoke() = module(ModuleContextData.Main) {
        factory<SqlDriver> {
            NativeSqliteDriver(
                schema = Database.Schema,
                name = get<DatabaseRepositoryModule.Config>(DATABASE_REPOSITORY_CONFIG).fileName
            )
        }
    }
}
