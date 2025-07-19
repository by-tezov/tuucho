package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.usecase.ComponentRenderUseCase
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterUpdateFormEventUseCase
import com.tezov.tuucho.core.domain.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.usecase.ValidatorFactoryUseCase
import org.koin.dsl.module

object UseCaseModule {

    internal operator fun invoke() = module {
        factory {
            ActionHandlerUseCase(
                get<CoroutineDispatchersProtocol>(),
                listOf(
                    get<NavigationUrlActionHandler>(),
                    get<FormSendUrlActionHandler>(),
                    get<FormUpdateActionHandler>(),
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
                get<NavigationUrlActionHandler>(),
                get<CoroutineDispatchersProtocol>()
            )
        }

        factory {
            RegisterUpdateFormEventUseCase(
                get<FormUpdateActionHandler>(),
                get<CoroutineDispatchersProtocol>()
            )
        }

        factory { RefreshCacheMaterialUseCase(get<MaterialRepositoryProtocol>()) }

        factory { SendDataUseCase(get<MaterialRepositoryProtocol>()) }

        factory { ValidatorFactoryUseCase() }

        factory { GetLanguageUseCase() }

    }

}


