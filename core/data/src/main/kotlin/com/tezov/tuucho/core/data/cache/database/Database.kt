package com.tezov.tuucho.core.data.cache.database

import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tezov.tuucho.core.data.cache.converter.JsonElementConverter
import com.tezov.tuucho.core.data.cache.dao.JsonEntityDao
import com.tezov.tuucho.core.data.cache.dao.JsonKeyValueDao
import com.tezov.tuucho.core.data.cache.dao.VersioningDao
import com.tezov.tuucho.core.data.cache.entity.JsonEntity
import com.tezov.tuucho.core.data.cache.entity.JsonKeyValueEntity
import com.tezov.tuucho.core.data.cache.entity.VersioningEntity

@androidx.room.Database(
    entities = [
        VersioningEntity::class,
        JsonKeyValueEntity::class,
        JsonEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(JsonElementConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun versioning(): VersioningDao

    abstract fun jsonKeyValue(): JsonKeyValueDao

    abstract fun jsonEntity(): JsonEntityDao

}
