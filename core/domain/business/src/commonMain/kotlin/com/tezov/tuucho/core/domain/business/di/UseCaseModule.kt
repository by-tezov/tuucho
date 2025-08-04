package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.business.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetLastViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase.InitiateAndRegisterToNavigationEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateForwardUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToFormUpdateEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToShadowerEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RenderViewContextUseCase
import com.tezov.tuucho.core.domain.business.usecase.RetrieveComponentUseCase
import com.tezov.tuucho.core.domain.business.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.AddFormUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.AddViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.IsFieldFormViewValidUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.RemoveFormFieldViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.UpdateFieldFormViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.UpdateViewUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

// TODO() // add new use case navigation

object UseCaseModule {

    internal operator fun invoke() = module {
        stateModule()

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

        factory<InitiateAndRegisterToNavigationEventUseCase> {
            InitiateAndRegisterToNavigationEventUseCase(
                coroutineScopes = get(),
                navigationUrlActionHandler = get(),
                navigateForward = get(),
            )
        }

        factory<NavigateForwardUseCase> {
            NavigateForwardUseCase(
                navigationRepository = get(),
                viewStackRepository = get(),
                retrieveComponent = get(),
            )
        }

        factory<RefreshMaterialCacheUseCase> {
            RefreshMaterialCacheUseCase(
                refreshMaterialCacheRepository = get()
            )
        }

        factory<RegisterToFormUpdateEventUseCase> {
            RegisterToFormUpdateEventUseCase(
                coroutineScopes = get(),
                formUpdateActionHandler = get(),
            )
        }

        factory<RegisterToShadowerEventUseCase> {
            RegisterToShadowerEventUseCase(
                coroutineScopes = get(),
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

        factory<RetrieveComponentUseCase> {
            RetrieveComponentUseCase(
                coroutineScopes = get(),
                updateMaterialState = get(),
                retrieveMaterialRepository = get(),
                registerShadowerEvent = get(),
                registerUpdateFormEvent = get(),
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
                getViewState = get()
            )
        }

        factory<AddViewUseCase> {
            AddViewUseCase(
                getViewState = get()
            )
        }

        factory<IsFieldFormViewValidUseCase> {
            IsFieldFormViewValidUseCase(
                getViewState = get()
            )
        }

        factory<RemoveFormFieldViewUseCase> {
            RemoveFormFieldViewUseCase(
                getViewState = get()
            )
        }

        factory<UpdateFieldFormViewUseCase> {
            UpdateFieldFormViewUseCase(
                getViewState = get()
            )
        }

        factory<UpdateViewUseCase> {
            UpdateViewUseCase(
                getViewState = get()
            )
        }

    }
}


