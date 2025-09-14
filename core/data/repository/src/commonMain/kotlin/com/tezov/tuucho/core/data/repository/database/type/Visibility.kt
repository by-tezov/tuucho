package com.tezov.tuucho.core.data.repository.database.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Visibility {

    companion object Type {
        const val local = "local"
        const val global = "global"
        const val contextual = "contextual"
    }

    abstract val name: String

    @Serializable
    @SerialName(local)
    data object Local : Visibility() {
        override val name = local
    }

    @Serializable
    @SerialName(global)
    data object Global : Visibility() {
        override val name = global
    }

    @Serializable
    @SerialName(contextual)
    data class Contextual(
        val urlOrigin: String,
    ) : Visibility() {
        override val name = contextual
    }

}