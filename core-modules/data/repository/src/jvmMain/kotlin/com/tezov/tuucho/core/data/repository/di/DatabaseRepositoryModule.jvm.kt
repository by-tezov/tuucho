package com.tezov.tuucho.core.data.repository.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.module.Module

object DatabaseRepositoryModuleJvm {
    internal fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            factory<SqlDriver> {
                val fileName = get<DatabaseRepositoryModule.Config>().fileName
                val liveRelativeFolderPath = get<SystemCoreDataModulesJvm.Config>().liveRelativeFolderPath
                val dbPath = "$liveRelativeFolderPath/$fileName".toPath()
                val parent = dbPath.parent
                if (parent != null && !FileSystem.SYSTEM.exists(parent)) {
                    FileSystem.SYSTEM.createDirectories(parent)
                }
                val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")
                if (!FileSystem.SYSTEM.exists(dbPath)) {
                    Database.Schema.create(driver)
                }
                driver
            }
        }
    }
}
