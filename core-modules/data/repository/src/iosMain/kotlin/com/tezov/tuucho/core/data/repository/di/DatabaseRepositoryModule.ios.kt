package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.tezov.tuucho.core.data.repository.database.Database
import org.koin.dsl.module

internal object DatabaseRepositoryModuleIos {

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupData.Main

        override fun Module.declaration() {

            factory<SqlDriver> {
                NativeSqliteDriver(
                    schema = Database.Schema,
                    name = get<DatabaseRepositoryModule.Config>().fileName
                )
            }
        }
    }

}
