package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.business.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.business.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterNavigationUrlEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterShadowerEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterUpdateFormEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RenderComponentUseCase
import com.tezov.tuucho.core.domain.business.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.AddFormUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.AddViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.InitializeViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.IsFieldFormViewValidUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.RemoveFormFieldViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.UpdateFieldFormViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.UpdateViewUseCase
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


