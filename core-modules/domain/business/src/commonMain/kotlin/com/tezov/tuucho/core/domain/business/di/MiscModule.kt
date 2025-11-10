package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.interaction.lock.ActionLockIdGenerator
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockRepository
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.datetime.ExpirationDateTimeRectifier
import com.tezov.tuucho.core.domain.tool.json.InstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.time.Instant

internal object MiscModule {
    fun invoke() = module(ModuleGroupDomain.Main) {
        single<Json> {
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = true

                serializersModule = SerializersModule {
                    contextual(Instant::class, InstantSerializer())
                }
            }
        }

        factory<ExpirationDateTimeRectifier> {
            ExpirationDateTimeRectifier()
        }

        single<InteractionLockRepositoryProtocol> {
            InteractionLockRepository(
                idGenerator = ActionLockIdGenerator()
            )
        }
    }
}
