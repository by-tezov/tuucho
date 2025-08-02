package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import org.koin.dsl.module

object ActionHandlerModule {

    internal operator fun invoke() = module {
        single {
            NavigationUrlActionHandler()
        }

        single {
            FormSendUrlActionHandler(
                materialState = get(),
                sendData = get()
            )
        }

        single { FormUpdateActionHandler() }
    }

}


