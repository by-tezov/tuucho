package com.tezov.tuucho.core.data.database

import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tezov.tuucho.core.data.database.converter.JsonObjectConverter
import com.tezov.tuucho.core.data.database.dao.JsonEntityDao
import com.tezov.tuucho.core.data.database.dao.VersioningDao
import com.tezov.tuucho.core.data.database.entity.JsonEntity
import com.tezov.tuucho.core.data.database.entity.VersioningEntity

@androidx.room.Database(
    entities = [
        VersioningEntity::class,
        JsonEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(JsonObjectConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun versioning(): VersioningDao

    abstract fun jsonEntity(): JsonEntityDao

}
