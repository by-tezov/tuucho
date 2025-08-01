package com.tezov.tuucho.core.data.database.type.adapter

import app.cash.sqldelight.ColumnAdapter
import com.tezov.tuucho.core.data.database.type.Visibility

class VisibilityAdapter: ColumnAdapter<Visibility, String> {

    override fun decode(databaseValue: String): Visibility {
        return Visibility.from(databaseValue)
    }

    override fun encode(value: Visibility): String {
        return value.to()
    }
}