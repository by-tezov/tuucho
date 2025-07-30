package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.FormSendUrlActionHandler
import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.usecase.ComponentRenderUseCase
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterShadowerEventUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterUpdateFormEventUseCase
import com.tezov.tuucho.core.domain.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.usecase.state.AddFormInMaterialStateUseCase
import com.tezov.tuucho.core.domain.usecase.state.AddScreenInMaterialStateUseCase
import com.tezov.tuucho.core.domain.usecase.state.ClearFormInMaterialStateUseCase
import com.tezov.tuucho.core.domain.usecase.state.InitializeMaterialStateUseCase
import com.tezov.tuucho.core.domain.usecase.state.IsFieldFormValidUseCase
import com.tezov.tuucho.core.domain.usecase.state.UpdateFieldFormUseCase
import com.tezov.tuucho.core.domain.usecase.state.UpdateMaterialStateUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

object UseCaseModule {

    internal operator fun invoke() = module {
        stateModule()

        single<ActionHandlerUseCase> {
            ActionHandlerUseCase(
                coroutineScopeProvider = get(),
                handlers = listOf(
                    get<NavigationUrlActionHandler>(),
                    get<FormSendUrlActionHandler>(),
                    get<FormUpdateActionHandler>(),
                )
            )
        }

        single<ComponentRenderUseCase> {
            ComponentRenderUseCase(
                coroutineScopeProvider = get(),
                initializeMaterialState = get(),
                updateMaterialState = get(),
                retrieveMaterialRepository = get(),
                screenRenderer = get(),
                registerShadowerEvent = get(),
                registerUpdateFormEvent = get()
            )
        }

        single<RegisterNavigationUrlEventUseCase> {
            RegisterNavigationUrlEventUseCase(
                coroutineScopeProvider = get(),
                navigationUrlActionHandler = get(),
            )
        }

        single<RegisterShadowerEventUseCase> {
            RegisterShadowerEventUseCase(
                coroutineScopeProvider = get(),
                shadowerMaterialRepository = get(),
            )
        }

        single<RegisterUpdateFormEventUseCase> {
            RegisterUpdateFormEventUseCase(
                coroutineScopeProvider = get(),
                formUpdateActionHandler = get(),
            )
        }

        single<RefreshCacheMaterialUseCase> {
            RefreshCacheMaterialUseCase(
                refreshCacheMaterialRepository = get()
            )
        }

        single<SendDataUseCase> {
            SendDataUseCase(
                sendDataAndRetrieveMaterialRepository = get()
            )
        }

        factory<ValidatorFactoryUseCase> { ValidatorFactoryUseCase() }

        single<GetLanguageUseCase> { GetLanguageUseCase() }

    }


    internal fun Module.stateModule() {

        single<AddFormInMaterialStateUseCase> {
            AddFormInMaterialStateUseCase(
                materialState = get()
            )
        }

        single<AddScreenInMaterialStateUseCase> {
            AddScreenInMaterialStateUseCase(
                materialState = get()
            )
        }

        single<ClearFormInMaterialStateUseCase> {
            ClearFormInMaterialStateUseCase(
                materialState = get()
            )
        }

        single<InitializeMaterialStateUseCase> {
            InitializeMaterialStateUseCase(
                materialState = get()
            )
        }

        single<IsFieldFormValidUseCase> {
            IsFieldFormValidUseCase(
                materialState = get()
            )
        }

        single<UpdateFieldFormUseCase> {
            UpdateFieldFormUseCase(
                materialState = get()
            )
        }

        single<UpdateMaterialStateUseCase> {
            UpdateMaterialStateUseCase(
                coroutineScopeProvider = get(),
                materialState = get()
            )
        }

    }
}


