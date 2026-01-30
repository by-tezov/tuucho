package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.IdGenerator
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockRegistry
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockResolver
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockStack
import com.tezov.tuucho.core.domain.business.middleware.MiddlewareExecutor
import com.tezov.tuucho.core.domain.business.model.action.FormActionDefinition
import com.tezov.tuucho.core.domain.business.model.action.NavigateActionDefinition
import com.tezov.tuucho.core.domain.business.model.action.StoreActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.tool.datetime.ExpirationDateTimeRectifier
import com.tezov.tuucho.core.domain.tool.json.InstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import kotlin.time.Instant

internal object MiscModule {
    fun invoke() = module(ModuleContextDomain.Main) {
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

        factoryOf(::ExpirationDateTimeRectifier)
        singleOf(::IdGenerator) bind IdGeneratorProtocol::class // <Unit, String>
        factoryOf(::MiddlewareExecutor) bind MiddlewareExecutorProtocol::class
        factoryOf(::InteractionLockGenerator)
        singleOf(::InteractionLockStack) bind InteractionLockProtocol.Stack::class
        factoryOf(::InteractionLockResolver) bind InteractionLockProtocol.Resolver::class

        single<InteractionLockProtocol.Registry> {
            InteractionLockRegistry(actionDefinitions = getAll())
        }
        factory { NavigateActionDefinition.Url } bind ActionDefinitionProtocol::class
        factory { NavigateActionDefinition.LocalDestination } bind ActionDefinitionProtocol::class
        factory { FormActionDefinition.Send } bind ActionDefinitionProtocol::class
        factory { FormActionDefinition.Update } bind ActionDefinitionProtocol::class
        factory { StoreActionDefinition.KeyValue } bind ActionDefinitionProtocol::class
    }
}
