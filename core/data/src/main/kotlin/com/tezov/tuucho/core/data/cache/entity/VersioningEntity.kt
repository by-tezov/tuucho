package com.tezov.tuucho.core.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_versioning")
data class VersioningEntity(
    @PrimaryKey val url: String,
    val version: String,
    val rootPrimaryKey: Long?,
    val isShared: Boolean,
)

