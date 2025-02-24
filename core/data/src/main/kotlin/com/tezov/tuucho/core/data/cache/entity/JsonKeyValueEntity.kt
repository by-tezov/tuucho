package com.tezov.tuucho.core.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.json.JsonElement

@Entity(tableName = "table_json_key_value")
data class JsonKeyValueEntity(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long? = null,
    val type: String,
    val url: String,
    val id: String,
    val idFrom: String?,
    val key: String,
    val jsonElement: JsonElement
)
