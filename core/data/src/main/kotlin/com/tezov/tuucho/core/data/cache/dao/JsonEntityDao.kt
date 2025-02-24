package com.tezov.tuucho.core.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tezov.tuucho.core.data.cache.entity.JsonEntity

@Dao
interface JsonEntityDao {

    @Query("SELECT * FROM table_json_entity")
    suspend fun selectAll(): List<JsonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(value: JsonEntity): Long

    @Query("SELECT * FROM table_json_entity WHERE primaryKey = :primaryKey")
    suspend fun find(primaryKey: Long): JsonEntity?

    @Query("SELECT * FROM table_json_entity WHERE type = :type AND url = :url AND id = :id")
    suspend fun find(type: String, url: String, id: String): JsonEntity?

    @Query(
        """
        SELECT * FROM table_json_entity 
        WHERE type = :type  AND id = :id 
        AND url IN (
            SELECT url FROM table_versioning 
            WHERE isShared = 1
        )
    """
    )
    suspend fun findShared(type: String, id: String): JsonEntity?

}