package com.tezov.tuucho.core.data.repository.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModule.Name.DATABASE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

object DatabaseRepositoryModuleAndroid {
    @OptIn(TuuchoInternalApi::class)
    internal fun invoke() = module(ModuleContextData.Main) {
        factory<SqlDriver> {
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = get(
                    SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT
                ),
                name = get<DatabaseRepositoryModule.Config>(DATABASE_REPOSITORY_CONFIG).fileName
            )
        }
    }
}
