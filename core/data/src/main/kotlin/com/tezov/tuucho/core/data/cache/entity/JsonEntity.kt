package com.tezov.tuucho.core.data.cache.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.json.JsonElement

@Entity(tableName = "table_json_entity")
data class JsonEntity(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long? = null,
    val type: String,
    val url: String,
    val id: String,
    val idFrom: String?,
    val jsonElement: JsonElement,
) {
    @Ignore
    var children: List<JsonEntity>? = null
}

fun JsonEntity.flatten(): List<JsonEntity> {
    val output = mutableListOf<JsonEntity>()
    val stack = ArrayDeque<JsonEntity>()
    stack.add(this)
    while (stack.isNotEmpty()) {
        val current = stack.removeLast()
        val children = current.children
        if(children.isNullOrEmpty()) {
            output.add(current)
        }
        else {
            stack.addAll(children.asReversed())
        }
    }
    return output
}
