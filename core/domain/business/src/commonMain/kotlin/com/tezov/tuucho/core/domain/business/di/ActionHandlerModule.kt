package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.NavigationLocalDestinationActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.NavigationUrlActionHandler
import org.koin.dsl.module

object ActionHandlerModule {

    internal operator fun invoke() = module {
        factory<FormSendUrlActionHandler> {
            FormSendUrlActionHandler(
                useCaseExecutor = get(),
                getOrNullScreen = get(),
                sendData = get()
            )
        }

        single<FormUpdateActionHandler> {
            FormUpdateActionHandler()
        }

        factory<NavigationLocalDestinationActionHandler> {
            NavigationLocalDestinationActionHandler(
                useCaseExecutor = get(),
                navigateBack = get()
            )
        }

        single<NavigationUrlActionHandler> {
            NavigationUrlActionHandler()
        }

    }
}


