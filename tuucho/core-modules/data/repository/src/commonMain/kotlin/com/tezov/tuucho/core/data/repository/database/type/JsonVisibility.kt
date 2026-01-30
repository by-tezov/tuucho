package com.tezov.tuucho.core.data.repository.database.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class JsonVisibility {
    companion object Type {
        const val local = "local"
        const val global = "global"
        const val contextual = "contextual"
    }

    abstract val name: String

    @Serializable
    @SerialName(local)
    data object Local : JsonVisibility() {
        override val name = local
    }

    @Serializable
    @SerialName(global)
    data object Global : JsonVisibility() {
        override val name = global
    }

    @Serializable
    @SerialName(contextual)
    data class Contextual(
        val urlOrigin: String,
    ) : JsonVisibility() {
        override val name = contextual
    }
}
