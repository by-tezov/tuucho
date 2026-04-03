package com.tezov.tuucho.core.data.repository.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.tezov.tuucho.core.data.repository._system.SystemPlatformFileJvm
import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.data.repository.di.DatabaseModule.Name.DATABASE_REPOSITORY_CONFIG
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import okio.Path.Companion.toPath

object DatabaseModuleJvm {
    @OptIn(TuuchoInternalApi::class)
    internal fun invoke() = module(ModuleContextData.Main) {
        single<SqlDriver> {
            val platform = get<SystemPlatformFileJvm>()
            val dbPath = platform.appFolder().resolve(
                get<DatabaseModule.Config>(DATABASE_REPOSITORY_CONFIG).fileName.toPath()
            )
            JdbcSqliteDriver(url = "jdbc:sqlite:$dbPath").also {
                if (!platform.fileSystem().exists(dbPath)) {
                    Database.Schema.create(it)
                }
            }
        }
    }
}
