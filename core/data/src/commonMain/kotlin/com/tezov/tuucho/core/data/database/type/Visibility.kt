package com.tezov.tuucho.core.data.database.type

enum class Visibility(val value:String) {
    Local("local"),
    Global("global");

    companion object {

        fun from(value: String): Visibility {
            return Visibility.entries.first { it.value == value }
        }
    }

    fun to(): String {
        return value
    }
}