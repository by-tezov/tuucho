package com.tezov.tuucho.core.data.repository.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import org.koin.core.module.Module

internal object DatabaseRepositoryModuleIos {
    fun invoke() = module(ModuleGroupData.Main) {
        factory<SqlDriver> {
            NativeSqliteDriver(
                schema = Database.Schema,
                name = get<DatabaseRepositoryModule.Config>().fileName
            )
        }
    }
}
