package com.tezov.tuucho.core.data.database.type

import com.tezov.tuucho.core.data.exception.DataException

sealed class Lifetime(val value: String) {
    object Unlimited : Lifetime("unlimited")
    class Transient(
        val urlOrigin: String,
    ) : Lifetime("transient")

    companion object {

        fun from(value: String): Lifetime {
            return when {
                value == "unlimited" -> Unlimited
                value.startsWith("transient:") -> {
                    Transient(urlOrigin = value.substringAfter("transient:"))
                }

                else -> throw DataException.Default("Unknown Lifetime value: $value")
            }
        }
    }

    fun to(): String {
        return when (this) {
            is Unlimited -> "unlimited"
            is Transient -> "transient:${urlOrigin}"
        }
    }
}