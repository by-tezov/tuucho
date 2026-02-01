package com.tezov.tuucho.core.data.repository.database

import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.TransactionWithoutReturn
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
internal class DatabaseTransactionFactory(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val database: Database,
) {
    suspend fun transaction(
        noEnclosing: Boolean = false,
        body: TransactionWithoutReturn.() -> Unit,
    ) {
        coroutineScopes.io.withContext {
            database.transaction(noEnclosing, body)
        }
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun <R> transactionWithResult(
        noEnclosing: Boolean = false,
        bodyWithReturn: TransactionWithReturn<R>.() -> R,
    ) = coroutineScopes.io.withContext {
        database.transactionWithResult(noEnclosing, bodyWithReturn)
    }
}
