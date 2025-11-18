package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.IdGenerator
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockRepository
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.datetime.ExpirationDateTimeRectifier
import com.tezov.tuucho.core.domain.tool.json.InstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.dsl.bind
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

        single {
            IdGenerator()
        } bind IdGeneratorProtocol::class // <Unit, String>

        factory<InteractionLockGenerator> {
            InteractionLockGenerator(
                idGenerator = get(),
            )
        }

        single<InteractionLockRepositoryProtocol> {
            InteractionLockRepository(
                coroutineScopes = get(),
                lockGenerator = get()
            )
        }
    }
}
