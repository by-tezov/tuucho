package com.tezov.tuucho.core.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.json.JsonObject

@Entity(tableName = "table_json_entity")
data class JsonEntity(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long? = null,
    val type: String,
    val url: String,
    val id: String,
    val idFrom: String?,
    val jsonObject: JsonObject,
)
