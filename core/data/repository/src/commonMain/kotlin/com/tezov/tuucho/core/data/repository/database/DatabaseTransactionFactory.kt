package com.tezov.tuucho.core.data.repository.database

import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.TransactionWithoutReturn

class DatabaseTransactionFactory(
    private val database: Database,
) {

    fun transaction(
        noEnclosing: Boolean = false,
        body: TransactionWithoutReturn.() -> Unit,
    ) {
        database.transaction(noEnclosing, body)
    }

    fun <R> transactionWithResult(
        noEnclosing: Boolean = false,
        bodyWithReturn: TransactionWithReturn<R>.() -> R,
    ): R {
        return database.transactionWithResult(noEnclosing, bodyWithReturn)
    }

}