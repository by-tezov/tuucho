package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.SendDataAndRetrieveMaterialRepositoryProtocol
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
                get<CoroutineContextProviderProtocol>(),
                listOf(
                    get<NavigationUrlActionHandler>(),
                    get<FormSendUrlActionHandler>(),
                    get<FormUpdateActionHandler>(),
                )
            )
        }

        factory {
            ComponentRenderUseCase(
                get<CoroutineContextProviderProtocol>(),
                get<MaterialStateProtocol>(),
                get<RetrieveMaterialRepositoryProtocol>(),
                get<ScreenRendererProtocol>(),
            )
        }

        factory {
            RegisterNavigationUrlEventUseCase(
                get<CoroutineContextProviderProtocol>(),
                get<NavigationUrlActionHandler>(),
            )
        }

        factory {
            RegisterUpdateFormEventUseCase(
                get<CoroutineContextProviderProtocol>(),
                get<FormUpdateActionHandler>(),
            )
        }

        factory {
            RefreshCacheMaterialUseCase(
                get<CoroutineContextProviderProtocol>(),
                get<RefreshCacheMaterialRepositoryProtocol>()
            )
        }

        factory {
            SendDataUseCase(
                get<CoroutineContextProviderProtocol>(),
                get<SendDataAndRetrieveMaterialRepositoryProtocol>()
            )
        }

        factory { ValidatorFactoryUseCase() }

        factory { GetLanguageUseCase() }

    }

}


