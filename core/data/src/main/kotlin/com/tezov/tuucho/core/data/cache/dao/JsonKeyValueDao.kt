package com.tezov.tuucho.core.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tezov.tuucho.core.data.cache.entity.JsonKeyValueEntity

@Dao
interface JsonKeyValueDao {

    @Query("SELECT * FROM table_json_key_value")
    suspend fun selectAll(): List<JsonKeyValueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(value: JsonKeyValueEntity): Long

    @Query("SELECT * FROM table_json_key_value WHERE type = :type AND url = :url AND id = :id AND `key` = :key")
    suspend fun find(type: String, url: String, id: String, key: String): JsonKeyValueEntity?

    @Query(
        """
        SELECT * FROM table_json_key_value 
        WHERE type = :type  AND id = :id AND `key` = :key
        AND url IN (
            SELECT url FROM table_versioning 
            WHERE isShared = 1
        )
    """
    )
    suspend fun findShared(type: String, id: String, key: String): JsonKeyValueEntity?

}