package com.tezov.tuucho.core.data.repository.database

import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.TransactionWithoutReturn
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
internal class DatabaseTransactionFactory(
    private val database: Database,
) {
    fun transaction(
        noEnclosing: Boolean = false,
        body: TransactionWithoutReturn.() -> Unit,
    ) {
        database.transaction(noEnclosing, body)
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun <R> transactionWithResult(
        noEnclosing: Boolean = false,
        bodyWithReturn: TransactionWithReturn<R>.() -> R,
    ): R = database.transactionWithResult(noEnclosing, bodyWithReturn)
}
