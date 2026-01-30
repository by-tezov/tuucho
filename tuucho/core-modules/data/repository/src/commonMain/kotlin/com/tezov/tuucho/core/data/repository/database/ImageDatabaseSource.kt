package com.tezov.tuucho.core.data.repository.database

import app.cash.sqldelight.TransactionWithoutReturn
import com.tezov.tuucho.core.data.repository.database.dao.ImageQueries
import com.tezov.tuucho.core.data.repository.database.entity.ImageEntity
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
internal class ImageDatabaseSource(
    private val transactionFactory: DatabaseTransactionFactory,
    private val imageQueries: ImageQueries,
) {
    fun selectAll() = imageQueries.selectAll()

    fun TransactionWithoutReturn.selectAll(
        cacheKeyPrefix: String,
    ) = imageQueries.selectAll(cacheKeyPrefix)

    @Suppress("RedundantSuspendModifier")
    suspend fun delete(
        cacheKey: String,
    ) {
        imageQueries.delete(cacheKey)
    }

    fun TransactionWithoutReturn.deleteAll(
        cacheKeyPrefix: String,
    ) {
        imageQueries.deleteAll(cacheKeyPrefix)
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun isExist(
        cacheKey: String
    ) = imageQueries.exist(cacheKey = cacheKey)

    @Suppress("RedundantSuspendModifier")
    suspend fun getImageEntityOrNull(
        cacheKey: String
    ) = imageQueries.getOrNull(cacheKey = cacheKey)

    @Suppress("RedundantSuspendModifier")
    suspend fun insertOrUpdate(
        entity: ImageEntity
    ) {
        transactionFactory.transaction {
            if (imageQueries.exist(entity.cacheKey)) {
                imageQueries.update(entity)
            } else {
                imageQueries.insert(entity)
            }
        }
    }
}
