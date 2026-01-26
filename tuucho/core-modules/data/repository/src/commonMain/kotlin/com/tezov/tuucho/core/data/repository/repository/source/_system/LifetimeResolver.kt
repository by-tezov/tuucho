@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.repository.source._system

import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TimeToLive
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.PageSettingSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.datetime.ExpirationDateTimeRectifier
import kotlinx.serialization.json.JsonObject
import kotlin.time.Instant

@OpenForTest
internal class LifetimeResolver(
    private val expirationDateTimeRectifier: ExpirationDateTimeRectifier,
) {
    fun invoke(
        pageSetting: JsonObject?,
        weakLifetime: Lifetime,
    ): Lifetime {
        val settingScope = pageSetting?.withScope(PageSettingSchema::Scope)
        val ttlScope = settingScope?.timeToLive?.withScope(TimeToLive::Scope)
        val lifetime = when {
            ttlScope != null -> {
                when (val strategy = ttlScope.strategy) {
                    TimeToLive.Value.Strategy.transient -> {
                        val expirationDateTime =
                            ttlScope.transientValue
                                ?.let { expirationDateTimeRectifier.process(it) }
                                ?.let { Instant.parse(it) }
                                ?: throw DataException.Default("ttl transient, missing property transient-value")
                        Lifetime.Transient(weakLifetime.validityKey, expirationDateTime)
                    }

                    TimeToLive.Value.Strategy.singleUse -> {
                        Lifetime.SingleUse(weakLifetime.validityKey)
                    }

                    else -> {
                        throw DataException.Default("unknown ttl strategy $strategy")
                    }
                }
            }

            weakLifetime is Lifetime.Enrolled -> {
                Lifetime.Unlimited(weakLifetime.validityKey)
            }

            else -> {
                weakLifetime
            }
        }
        return lifetime
    }
}
