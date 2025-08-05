package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.business.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetLastViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToNavigationUrlActionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToViewStackRepositoryEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterUpdateViewEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RenderViewContextUseCase
import com.tezov.tuucho.core.domain.business.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.state.AddFormUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.AddViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.IsFieldFormViewValidUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.RemoveFormFieldViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.UpdateFieldFormViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.UpdateViewUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

object UseCaseModule {

    internal operator fun invoke() = module {
        stateModule()

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
                    get<FormSendUrlActionHandler>(),
                    get<FormUpdateActionHandler>(),
                )
            )
        }

        factory<GetLanguageUseCase> { GetLanguageUseCase() }

        factory<GetLastViewUseCase> {
            GetLastViewUseCase(
                viewStackRepository = get()
            )
        }

        factory<GetViewStateUseCase> {
            GetViewStateUseCase(
                viewStackRepository = get()
            )
        }

        factory<NavigateToUrlUseCase> {
            NavigateToUrlUseCase(
                coroutineScopes = get(),
                retrieveMaterialRepository = get(),
                navigationStackRepository = get(),
                viewContextStackRepository = get()
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

        factory<RegisterToViewStackRepositoryEventUseCase> {
            RegisterToViewStackRepositoryEventUseCase(
                coroutineScopes = get(),
                viewContextStackRepository = get()
            )
        }

        factory<RegisterUpdateViewEventUseCase> {
            RegisterUpdateViewEventUseCase(
                coroutineScopes = get(),
                updateViewUseCase = get(),
                formUpdateActionHandler = get(),
                shadowerMaterialRepository = get(),
            )
        }

        factory<RenderViewContextUseCase> {
            RenderViewContextUseCase(
                coroutineScopes = get(),
                stateViewFactory = get(),
                componentRenderer = get(),
            )
        }

        factory<SendDataUseCase> {
            SendDataUseCase(
                sendDataAndRetrieveMaterialRepository = get()
            )
        }

        factory<ValidatorFactoryUseCase> { ValidatorFactoryUseCase() }

    }


    internal fun Module.stateModule() {

        factory<AddFormUseCase> {
            AddFormUseCase(
                useCaseExecutor = get(),
                getViewState = get()
            )
        }

        factory<AddViewUseCase> {
            AddViewUseCase(
                useCaseExecutor = get(),
                getViewState = get()
            )
        }

        factory<IsFieldFormViewValidUseCase> {
            IsFieldFormViewValidUseCase(
                useCaseExecutor = get(),
                getViewState = get()
            )
        }

        factory<RemoveFormFieldViewUseCase> {
            RemoveFormFieldViewUseCase(
                useCaseExecutor = get(),
                getViewState = get()
            )
        }

        factory<UpdateFieldFormViewUseCase> {
            UpdateFieldFormViewUseCase(
                useCaseExecutor = get(),
                getViewState = get()
            )
        }

        factory<UpdateViewUseCase> {
            UpdateViewUseCase(
                useCaseExecutor = get(),
                getViewState = get()
            )
        }

    }
}


