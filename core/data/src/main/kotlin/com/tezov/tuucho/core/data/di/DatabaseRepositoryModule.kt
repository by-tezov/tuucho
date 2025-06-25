package com.tezov.tuucho.core.data.di

import androidx.room.Room
import com.tezov.tuucho.core.data.database.Database
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object DatabaseRepositoryModule {

    internal operator fun invoke() = module {
        single<Database> {
            val databaseName = "database.db"

            //TODO remove and used migration when done
            val context = androidContext()
            val dbFile = context.getDatabasePath(databaseName)
            if (dbFile.exists()) {
                dbFile.delete()
            }

            Room.databaseBuilder(
                androidContext(),
                Database::class.java,
                databaseName
            ).build()
        }
    }

}
