package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.SendFormUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.UpdateFormActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.usecase.ComponentRenderUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
import com.tezov.tuucho.core.domain.usecase.SendDataUseCase
import org.koin.dsl.module

object UseCaseModule {

    internal operator fun invoke() = module {
        factory {
            ActionHandlerUseCase(
                get<CoroutineDispatchersProtocol>(),
                listOf(
                    get<NavigationUrlActionHandler>(),
                    get<SendFormUrlActionHandler>(),
                    get<UpdateFormActionHandler>(),
                )
            )
        }

        factory {
            ComponentRenderUseCase(
                get<MaterialStateProtocol>(),
                get<MaterialRepositoryProtocol>(),
                get<ScreenRendererProtocol>(),
            )
        }

        factory {
            RegisterNavigationUrlEventUseCase(
                get<NavigationUrlActionHandler>()
            )
        }

        factory { RefreshCacheMaterialUseCase(get<MaterialRepositoryProtocol>()) }

        factory { SendDataUseCase(get<MaterialRepositoryProtocol>()) }

        factory { UpdateFormActionHandler(get<MaterialStateProtocol>()) }
    }

}


