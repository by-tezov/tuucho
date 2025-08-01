package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
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
                coroutineScopeProvider = get(),
                handlers = listOf(
                    get<NavigationUrlActionHandler>(),
                    get<FormSendUrlActionHandler>(),
                    get<FormUpdateActionHandler>(),
                )
            )
        }

        factory<RenderComponentUseCase> {
            RenderComponentUseCase(
                coroutineScopeProvider = get(),
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
                coroutineScopeProvider = get(),
                navigationUrlActionHandler = get(),
            )
        }

        factory<RegisterShadowerEventUseCase> {
            RegisterShadowerEventUseCase(
                coroutineScopeProvider = get(),
                shadowerMaterialRepository = get(),
            )
        }

        factory<RegisterUpdateFormEventUseCase> {
            RegisterUpdateFormEventUseCase(
                coroutineScopeProvider = get(),
                formUpdateActionHandler = get(),
            )
        }

        factory<RefreshCacheMaterialUseCase> {
            RefreshCacheMaterialUseCase(
                refreshCacheMaterialRepository = get()
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
                screenState = get()
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


