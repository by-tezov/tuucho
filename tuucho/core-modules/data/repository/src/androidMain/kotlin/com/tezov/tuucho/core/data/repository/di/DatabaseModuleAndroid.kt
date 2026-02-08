package com.tezov.tuucho.core.data.repository.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.data.repository.di.DatabaseModule.Name.DATABASE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

object DatabaseModuleAndroid {
    @OptIn(TuuchoInternalApi::class)
    internal fun invoke() = module(ModuleContextData.Main) {
        factory<SqlDriver> {
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = get(
                    PlatformModuleAndroid.Name.APPLICATION_CONTEXT
                ),
                name = get<DatabaseModule.Config>(DATABASE_REPOSITORY_CONFIG).fileName
            )
        }
    }
}
