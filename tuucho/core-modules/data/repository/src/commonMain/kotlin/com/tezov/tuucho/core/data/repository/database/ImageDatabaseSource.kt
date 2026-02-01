package com.tezov.tuucho.core.data.repository.database

import app.cash.sqldelight.TransactionWithoutReturn
import com.tezov.tuucho.core.data.repository.database.dao.ImageQueries
import com.tezov.tuucho.core.data.repository.database.entity.ImageEntity
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
internal class ImageDatabaseSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val databaseTransactionFactory: DatabaseTransactionFactory,
    private val imageQueries: ImageQueries,
) {
    suspend fun selectAll() = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            imageQueries.selectAll()
        }
    }

    fun TransactionWithoutReturn.selectAll(
        cacheKeyPrefix: String,
    ) = imageQueries.selectAll(cacheKeyPrefix)

    suspend fun delete(
        cacheKey: String,
    ) {
        coroutineScopes.io.withContext {
            databaseTransactionFactory.transaction {
                imageQueries.delete(cacheKey)
            }
        }
    }

    fun TransactionWithoutReturn.deleteAll(
        cacheKeyPrefix: String,
    ) {
        imageQueries.deleteAll(cacheKeyPrefix)
    }

    suspend fun isExist(
        cacheKey: String
    ) = coroutineScopes.io.withContext {
        imageQueries.exist(cacheKey = cacheKey)
    }

    suspend fun getImageEntityOrNull(
        cacheKey: String
    ) = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            imageQueries.getOrNull(cacheKey = cacheKey)
        }
    }

    suspend fun insertOrUpdate(
        entity: ImageEntity
    ) {
        coroutineScopes.io.withContext {
            databaseTransactionFactory.transaction {
                if (imageQueries.exist(entity.cacheKey)) {
                    imageQueries.update(entity)
                } else {
                    imageQueries.insert(entity)
                }
            }
        }
    }
}
