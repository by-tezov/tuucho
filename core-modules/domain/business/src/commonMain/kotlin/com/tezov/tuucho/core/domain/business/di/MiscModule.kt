package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.interaction.lock.ActionLockIdGenerator
import com.tezov.tuucho.core.domain.business.interaction.lock.InterractionLockRepository
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InterractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.datetime.ExpirationDateTimeRectifier
import com.tezov.tuucho.core.domain.tool.json.InstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.core.module.Module
import kotlin.time.Instant

internal object MiscModule {

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupDomain.Main

        override fun Module.declaration() {
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

            single<InterractionLockRepositoryProtocol> {
                InterractionLockRepository(
                    idGenerator = ActionLockIdGenerator()
                )
            }
        }
    }

}


