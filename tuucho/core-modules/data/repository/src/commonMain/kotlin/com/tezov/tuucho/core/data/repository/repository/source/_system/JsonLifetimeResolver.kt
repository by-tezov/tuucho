@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.repository.source._system

import com.tezov.tuucho.core.data.repository.database.type.JsonLifetime
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TimeToLive
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.datetime.ExpirationDateTimeRectifier
import kotlinx.serialization.json.JsonObject
import kotlin.time.Instant

@OpenForTest
internal class JsonLifetimeResolver(
    private val expirationDateTimeRectifier: ExpirationDateTimeRectifier,
) {
    fun invoke(
        timeToLiveObject: JsonObject?,
        weakLifetime: JsonLifetime,
    ): JsonLifetime {
        val ttlScope = timeToLiveObject?.withScope(TimeToLive::Scope)
        val lifetime = when {
            ttlScope != null -> {
                when (val strategy = ttlScope.strategy) {
                    TimeToLive.Value.Strategy.transient -> {
                        val expirationDateTime =
                            ttlScope.transientValue
                                ?.let { expirationDateTimeRectifier.process(it) }
                                ?.let { Instant.parse(it) }
                                ?: throw DataException.Default("ttl transient, missing property transient-value")
                        JsonLifetime.Transient(weakLifetime.validityKey, expirationDateTime)
                    }

                    TimeToLive.Value.Strategy.singleUse -> {
                        JsonLifetime.SingleUse(weakLifetime.validityKey)
                    }

                    else -> {
                        throw DataException.Default("unknown ttl strategy $strategy")
                    }
                }
            }

            weakLifetime is JsonLifetime.Enrolled -> {
                JsonLifetime.Unlimited(weakLifetime.validityKey)
            }

            else -> {
                weakLifetime
            }
        }
        return lifetime
    }
}
