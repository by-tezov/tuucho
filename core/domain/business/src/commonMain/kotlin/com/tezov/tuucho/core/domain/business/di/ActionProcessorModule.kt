package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.interaction.action.FormSendUrlActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.FormUpdateActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.NavigationLocalDestinationActionProcessor
import com.tezov.tuucho.core.domain.business.interaction.action.NavigationUrlActionProcessor
import org.koin.dsl.module

object ActionProcessorModule {

    internal operator fun invoke() = module {
        factory<FormSendUrlActionProcessor> {
            FormSendUrlActionProcessor(
                useCaseExecutor = get(),
                getOrNullScreen = get(),
                sendData = get()
            )
        }

        single<FormUpdateActionProcessor> {
            FormUpdateActionProcessor(
                useCaseExecutor = get(),
                updateView = get()
            )
        }

        factory<NavigationLocalDestinationActionProcessor> {
            NavigationLocalDestinationActionProcessor(
                useCaseExecutor = get(),
                navigateBack = get()
            )
        }

        single<NavigationUrlActionProcessor> {
            NavigationUrlActionProcessor(
                useCaseExecutor = get(),
                navigateForward = get()
            )
        }

    }
}


