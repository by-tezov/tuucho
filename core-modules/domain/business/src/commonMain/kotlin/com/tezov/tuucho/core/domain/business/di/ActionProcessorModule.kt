package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.interaction.action.FormSendUrlActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.FormUpdateActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.NavigationLocalDestinationActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.NavigationUrlActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.StoreActionProcessor
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import org.koin.core.module.Module
import org.koin.dsl.bind

internal object ActionProcessorModule {

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupDomain.ActionProcessor

        override fun Module.declaration() {

            factory<FormSendUrlActionProcessor> {
                FormSendUrlActionProcessor(
                    useCaseExecutor = get(),
                    getOrNullScreen = get(),
                    sendData = get()
                )
            } bind ActionProcessorProtocol::class

            factory<FormUpdateActionProcessor> {
                FormUpdateActionProcessor(
                    useCaseExecutor = get(),
                    updateView = get()
                )
            } bind ActionProcessorProtocol::class

            factory<NavigationLocalDestinationActionProcessor> {
                NavigationLocalDestinationActionProcessor(
                    useCaseExecutor = get(),
                    navigateBack = get()
                )
            } bind ActionProcessorProtocol::class

            factory<NavigationUrlActionProcessor> {
                NavigationUrlActionProcessor(
                    useCaseExecutor = get(),
                    navigateToUrl = get()
                )
            } bind ActionProcessorProtocol::class

            factory<StoreActionProcessor> {
                StoreActionProcessor(
                    useCaseExecutor = get(),
                    saveKeyValueToStore = get(),
                    removeKeyValueFromStore = get()
                )
            } bind ActionProcessorProtocol::class

        }
    }
}


