package com.tezov.tuucho.core.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.di.DatabaseRepositoryModule.databaseName
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.*

object DatabaseRepositoryModuleIos {

    @OptIn(ExperimentalForeignApi::class)
    internal operator fun invoke() = module {

        factory<SqlDriver> {
            //TODO remove and use migration when done
            val fileManager = NSFileManager.defaultManager
            val urls = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
            val documentsDirectory = urls.firstOrNull() as? NSURL
            documentsDirectory?.let { dir ->
                val dbUrl = dir.URLByAppendingPathComponent(
                    databaseName
                )
                val path = dbUrl?.path
                if (path != null && fileManager.fileExistsAtPath(path)) {
                    fileManager.removeItemAtURL(dbUrl, null)
                }
            }

            NativeSqliteDriver(
                schema = Database.Schema,
                name = databaseName
            )
        }
    }

}
