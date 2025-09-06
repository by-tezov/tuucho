package com.tezov.tuucho.core.data.database.entity

import com.tezov.tuucho.core.data.database.statement.GetValidityKeyAndValidityDateTimeByUrl
import kotlin.time.Instant

data class ValidityEntity(
    val key: String?,
    val expirationDateTime: Instant?,
)

fun GetValidityKeyAndValidityDateTimeByUrl.toEntity() = ValidityEntity(
    key = validityKey,
    expirationDateTime = expirationDateTime?.let {
        Instant.parse(it)
    },
)