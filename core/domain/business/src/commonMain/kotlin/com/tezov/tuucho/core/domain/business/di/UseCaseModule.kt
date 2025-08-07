package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.NavigationLocalDestinationActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.business.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetOrNullScreenUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToNavigationUrlActionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToScreenStackRepositoryEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterUpdateViewEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import org.koin.dsl.module

object UseCaseModule {

    internal operator fun invoke() = module {

        single<UseCaseExecutor> {
            UseCaseExecutor(
                coroutineScopes = get()
            )
        }

        factory<ActionHandlerUseCase> {
            ActionHandlerUseCase(
                coroutineScopes = get(),
                handlers = listOf(
                    get<NavigationUrlActionHandler>(),
                    get<NavigationLocalDestinationActionHandler>(),
                    get<FormSendUrlActionHandler>(),
                    get<FormUpdateActionHandler>(),
                )
            )
        }

        factory<GetLanguageUseCase> { GetLanguageUseCase() }

        factory<GetOrNullScreenUseCase> {
            GetOrNullScreenUseCase(
                navigationScreenStackRepository = get()
            )
        }

        factory<NavigateBackUseCase> {
            NavigateBackUseCase(
                coroutineScopes = get(),
                navigationDestinationStackRepository = get(),
                navigationScreenStackRepository = get(),
            )
        }

        factory<NavigateToUrlUseCase> {
            NavigateToUrlUseCase(
                coroutineScopes = get(),
                retrieveMaterialRepository = get(),
                navigationDestinationStackRepository = get(),
                navigationScreenStackRepository = get(),
                shadowerMaterialRepository = get()
            )
        }

        factory<RefreshMaterialCacheUseCase> {
            RefreshMaterialCacheUseCase(
                refreshMaterialCacheRepository = get()
            )
        }

        factory<RegisterToNavigationUrlActionEventUseCase> {
            RegisterToNavigationUrlActionEventUseCase(
                coroutineScopes = get(),
                useCaseExecutor = get(),
                navigationUrlActionHandler = get(),
                navigateForward = get(),
            )
        }

        factory<RegisterToScreenStackRepositoryEventUseCase> {
            RegisterToScreenStackRepositoryEventUseCase(
                coroutineScopes = get(),
                navigationScreenStackRepository = get()
            )
        }

        factory<RegisterUpdateViewEventUseCase> {
            RegisterUpdateViewEventUseCase(
                coroutineScopes = get(),
                navigationScreenStackRepository = get(),
                formUpdateActionHandler = get(),
                shadowerMaterialRepository = get()
            )
        }

        factory<SendDataUseCase> {
            SendDataUseCase(
                sendDataAndRetrieveMaterialRepository = get()
            )
        }

        factory<ValidatorFactoryUseCase> { ValidatorFactoryUseCase() }

    }
}


