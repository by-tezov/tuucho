package com.tezov.tuucho.core.data.repository.database.dao

import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.data.repository.database.entity.ImageEntity
import com.tezov.tuucho.core.data.repository.database.entity.toEntity
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
internal class ImageQueries(
    private val database: Database
) {
    private val queries get() = database.imageStatementQueries

    fun selectAll() = queries.selectAll().executeAsList().map { it.toEntity() }

    fun selectAll(
        cacheKeyPrefix: String
    ) = queries.selectAllLikeCacheKey("$cacheKeyPrefix%").executeAsList().map { it.toEntity() }

    fun delete(
        cacheKey: String
    ) {
        queries.deleteByCacheKey(cacheKey)
    }

    fun deleteAll() = queries.deleteAll()

    fun deleteAll(
        cacheKeyPrefix: String
    ) {
        queries.deleteAllLikeCacheKey("$cacheKeyPrefix%")
    }

    fun exist(
        cacheKey: String
    ) = queries.existWithCacheKey(cacheKey).executeAsOneOrNull() == true

    fun insert(
        entity: ImageEntity
    ) {
        queries.insert(
            cacheKey = entity.cacheKey,
            mimeType = entity.mimeType,
        )
    }

    fun update(
        entity: ImageEntity
    ) {
        queries.updateImageByCacheKey(
            cacheKey = entity.cacheKey,
            mimeType = entity.mimeType,
        )
    }

    fun getOrNull(
        cacheKey: String
    ) = queries.getByCacheKey(cacheKey).executeAsOneOrNull()?.toEntity()
}
