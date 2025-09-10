package com.tezov.tuucho.core.data.database.type

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
sealed class Lifetime {

    companion object Type {
        const val enrolled = "enrolled"
        const val unlimited = "unlimited"
        const val transient = "transient"
    }

    abstract val name: String
    abstract val validityKey: String?

    @Serializable
    @SerialName(enrolled)
    data class Enrolled(
        override val validityKey: String?,
    ) : Lifetime() {
        override val name = transient
    }

    @Serializable
    @SerialName(unlimited)
    data class Unlimited(override val validityKey: String?) : Lifetime() {
        override val name = unlimited
    }

    @Serializable
    @SerialName(transient)
    data class Transient(
        override val validityKey: String?,
        @Contextual val expirationDateTime: Instant,
    ) : Lifetime() {
        override val name = transient
    }

}