package com.tezov.tuucho.core.data.database.type.adapter

import app.cash.sqldelight.ColumnAdapter
import com.tezov.tuucho.core.data.database.type.Lifetime

class LifetimeAdapter: ColumnAdapter<Lifetime, String> {

    override fun decode(databaseValue: String): Lifetime {
        return Lifetime.from(databaseValue)
    }

    override fun encode(value: Lifetime): String {
        return value.to()
    }
}