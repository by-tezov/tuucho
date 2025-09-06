package com.tezov.tuucho.core.data.parser.breaker._system

import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import kotlinx.serialization.json.JsonObject

fun interface VersioningEntityFactoryProtocol {
    operator fun invoke(pageSetting: JsonObject?): VersioningEntity
}