package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.SendFormUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.UpdateFormActionHandler
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.SendDataUseCase
import org.koin.dsl.module

object ActionHandlerModule {

    internal operator fun invoke() = module {
        single {
            NavigationUrlActionHandler()
        }

        single {
            SendFormUrlActionHandler(
                get<MaterialStateProtocol>(),
                get<SendDataUseCase>()
            )
        }

        factory { UpdateFormActionHandler(get<MaterialStateProtocol>()) }
    }

}


