package com.tezov.tuucho.core.data.repository.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.module.Module

object DatabaseRepositoryModuleAndroid {
    @OptIn(TuuchoInternalApi::class)
    internal fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            factory<SqlDriver> {
                AndroidSqliteDriver(
                    schema = Database.Schema,
                    context = get(SystemCoreDataModulesAndroid.Name.APPLICATION_CONTEXT),
                    name = get<DatabaseRepositoryModule.Config>().fileName
                )
            }
        }
    }
}
