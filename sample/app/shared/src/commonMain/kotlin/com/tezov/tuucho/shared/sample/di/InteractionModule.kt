package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.di.ModuleGroupDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.shared.sample.interaction.LoggerInteraction
import org.koin.dsl.bind

object InteractionModule {

    fun invoke() = module(ModuleGroupDomain.ActionProcessor) {
        factory<LoggerInteraction> {
            LoggerInteraction(
                logger = get(),
            )
        }  bind ActionProcessorProtocol::class

    }

}
