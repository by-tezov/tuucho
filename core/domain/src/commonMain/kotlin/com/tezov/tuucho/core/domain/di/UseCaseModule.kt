package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterShadowerEventUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterUpdateFormEventUseCase
import com.tezov.tuucho.core.domain.usecase.RenderComponentUseCase
import com.tezov.tuucho.core.domain.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.usecase.state.AddFormUseCase
import com.tezov.tuucho.core.domain.usecase.state.AddViewUseCase
import com.tezov.tuucho.core.domain.usecase.state.InitializeViewStateUseCase
import com.tezov.tuucho.core.domain.usecase.state.IsFieldFormViewValidUseCase
import com.tezov.tuucho.core.domain.usecase.state.RemoveFormFieldViewUseCase
import com.tezov.tuucho.core.domain.usecase.state.UpdateFieldFormViewUseCase
import com.tezov.tuucho.core.domain.usecase.state.UpdateViewUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

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

        factory<RenderComponentUseCase> {
            RenderComponentUseCase(
                coroutineScopes = get(),
                initializeMaterialState = get(),
                updateMaterialState = get(),
                retrieveMaterialRepository = get(),
                screenRenderer = get(),
                registerShadowerEvent = get(),
                registerUpdateFormEvent = get()
            )
        }

        factory<RegisterNavigationUrlEventUseCase> {
            RegisterNavigationUrlEventUseCase(
                coroutineScopes = get(),
                navigationUrlActionHandler = get(),
            )
        }

        factory<RegisterShadowerEventUseCase> {
            RegisterShadowerEventUseCase(
                coroutineScopes = get(),
                shadowerMaterialRepository = get(),
            )
        }

        factory<RegisterUpdateFormEventUseCase> {
            RegisterUpdateFormEventUseCase(
                coroutineScopes = get(),
                formUpdateActionHandler = get(),
            )
        }

        factory<RefreshMaterialCacheUseCase> {
            RefreshMaterialCacheUseCase(
                refreshMaterialCacheRepository = get()
            )
        }

        factory<SendDataUseCase> {
            SendDataUseCase(
                sendDataAndRetrieveMaterialRepository = get()
            )
        }

        factory<ValidatorFactoryUseCase> { ValidatorFactoryUseCase() }

        factory<GetLanguageUseCase> { GetLanguageUseCase() }

    }


    internal fun Module.stateModule() {

        factory<AddFormUseCase> {
            AddFormUseCase(
                screenState = get()
            )
        }

        factory<AddViewUseCase> {
            AddViewUseCase(
                screenState = get()
            )
        }

        factory<RemoveFormFieldViewUseCase> {
            RemoveFormFieldViewUseCase(
                screenState = get()
            )
        }

        factory<InitializeViewStateUseCase> {
            InitializeViewStateUseCase(
                screenState = get(),
                clearTransientMaterialCacheRepository = get()
            )
        }

        factory<IsFieldFormViewValidUseCase> {
            IsFieldFormViewValidUseCase(
                screenState = get()
            )
        }

        factory<UpdateFieldFormViewUseCase> {
            UpdateFieldFormViewUseCase(
                screenState = get()
            )
        }

        factory<UpdateViewUseCase> {
            UpdateViewUseCase(
                screenState = get()
            )
        }

    }
}


