package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.NavigationUrlActionHandler
import org.koin.dsl.module

object ActionHandlerModule {

    internal operator fun invoke() = module {
        single {
            NavigationUrlActionHandler()
        }

        single {
            FormSendUrlActionHandler(
                useCaseExecutor = get(),
                getViewState = get(),
                sendData = get()
            )
        }

        single { FormUpdateActionHandler() }
    }

}


