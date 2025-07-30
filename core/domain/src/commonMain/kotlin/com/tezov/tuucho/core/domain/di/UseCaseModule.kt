package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.SendDataAndRetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ShadowerMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.usecase.ComponentRenderUseCase
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterShadowerEventUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterUpdateFormEventUseCase
import com.tezov.tuucho.core.domain.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.usecase.ValidatorFactoryUseCase
import org.koin.dsl.module

object UseCaseModule {

    internal operator fun invoke() = module {
        single {
            ActionHandlerUseCase(
                get<CoroutineContextProviderProtocol>(),
                listOf(
                    get<NavigationUrlActionHandler>(),
                    get<FormSendUrlActionHandler>(),
                    get<FormUpdateActionHandler>(),
                )
            )
        }

        single {
            ComponentRenderUseCase(
                get<CoroutineContextProviderProtocol>(),
                get<MaterialStateProtocol>(),
                get<RetrieveMaterialRepositoryProtocol>(),
                get<ScreenRendererProtocol>(),
                get<RegisterShadowerEventUseCase>()
            )
        }

        single {
            RegisterNavigationUrlEventUseCase(
                get<CoroutineContextProviderProtocol>(),
                get<NavigationUrlActionHandler>(),
            )
        }

        single {
            RegisterShadowerEventUseCase(
                get<CoroutineContextProviderProtocol>(),
                get<ShadowerMaterialRepositoryProtocol>(),
            )
        }

        single {
            RegisterUpdateFormEventUseCase(
                get<CoroutineContextProviderProtocol>(),
                get<FormUpdateActionHandler>(),
            )
        }

        single {
            RefreshCacheMaterialUseCase(
                get<RefreshCacheMaterialRepositoryProtocol>()
            )
        }

        single {
            SendDataUseCase(
                get<SendDataAndRetrieveMaterialRepositoryProtocol>()
            )
        }

        factory { ValidatorFactoryUseCase() }

        single { GetLanguageUseCase() }

    }

}


