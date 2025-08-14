package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.action.FormSendUrlActionProcessor
import com.tezov.tuucho.core.domain.business.action.FormUpdateActionProcessor
import com.tezov.tuucho.core.domain.business.action.NavigationLocalDestinationActionProcessor
import com.tezov.tuucho.core.domain.business.action.NavigationUrlActionProcessor
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
                coroutineScopes = get(),
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
                coroutineScopes = get(),
            )
        }

    }
}


