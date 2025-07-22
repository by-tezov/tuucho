package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.SendDataUseCase
import org.koin.dsl.module

object ActionHandlerModule {

    internal operator fun invoke() = module {
        single {
            NavigationUrlActionHandler()
        }

        single {
            FormSendUrlActionHandler(
                get<MaterialStateProtocol>(),
                get<SendDataUseCase>()
            )
        }

        single { FormUpdateActionHandler() }
    }

}


