package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.interaction.action.FormSendUrlActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.FormUpdateActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.NavigationLocalDestinationActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.NavigationUrlActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.StoreActionProcessor
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

internal object ActionProcessorModule {

    object Name {
        val PROCESSORS = named("ActionProcessorModule.Name.PROCESSORS")
    }

    fun invoke() = module {
        factory<List<ActionProcessorProtocol>>(Name.PROCESSORS) {
            getAll<ActionProcessorProtocol>()
        }

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
                navigateForward = get()
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


