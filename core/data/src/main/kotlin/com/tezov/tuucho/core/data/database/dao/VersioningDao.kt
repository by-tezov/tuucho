package com.tezov.tuucho.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tezov.tuucho.core.data.database.entity.VersioningEntity

@Dao
interface VersioningDao {

    @Query("SELECT * FROM table_versioning")
    suspend fun selectAll(): List<VersioningEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(value: VersioningEntity)

    @Query("SELECT version FROM table_versioning WHERE url = :url")
    suspend fun version(url: String): String?

    @Query("SELECT * FROM table_versioning WHERE url = :url")
    suspend fun find(url: String): VersioningEntity?
}