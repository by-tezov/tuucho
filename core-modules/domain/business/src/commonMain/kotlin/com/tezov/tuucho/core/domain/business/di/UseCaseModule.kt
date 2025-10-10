package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.di.NavigationModule.Name
import com.tezov.tuucho.core.domain.business.protocol.HookProtocol
import com.tezov.tuucho.core.domain.business.usecase.FormValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetScreenOrNullUseCase
import com.tezov.tuucho.core.domain.business.usecase.GetScreensFromRoutesUseCase
import com.tezov.tuucho.core.domain.business.usecase.GeyValueFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.GeyValueOrNullFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.HasKeyInStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigationDefinitionSelectorMatcherFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.NavigationStackTransitionHelperFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.NotifyNavigationTransitionCompletedUseCase
import com.tezov.tuucho.core.domain.business.usecase.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.RegisterToScreenTransitionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.RemoveKeyValueFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.SaveKeyValueToStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.UpdateViewUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import org.koin.dsl.module

internal object UseCaseModule {

    fun invoke() = module {

        single<UseCaseExecutor> {
            UseCaseExecutor(
                coroutineScopes = get()
            )
        }

        factory<FormValidatorFactoryUseCase> { FormValidatorFactoryUseCase() }

        factory<GetLanguageUseCase> { GetLanguageUseCase() }

        factory<GetScreenOrNullUseCase> {
            GetScreenOrNullUseCase(
                navigationStackScreenRepository = get()
            )
        }

        factory<GetScreensFromRoutesUseCase> {
            GetScreensFromRoutesUseCase(
                navigationStackScreenRepository = get(),
            )
        }

        factory<GeyValueFromStoreUseCase> {
            GeyValueFromStoreUseCase(
                coroutineScopes = get(),
                keyValueRepository = get()
            )
        }

        factory<GeyValueOrNullFromStoreUseCase> {
            GeyValueOrNullFromStoreUseCase(
                coroutineScopes = get(),
                keyValueRepository = get()
            )
        }

        factory<HasKeyInStoreUseCase> {
            HasKeyInStoreUseCase(
                coroutineScopes = get(),
                keyValueRepository = get()
            )
        }

        factory<NavigateBackUseCase> {
            NavigateBackUseCase(
                coroutineScopes = get(),
                navigationStackRouteRepository = get(),
                navigationStackScreenRepository = get(),
                navigationStackTransitionRepository = get(),
                shadowerMaterialRepository = get(),
                actionLockRepository = get(),
                hookBeforeNavigation = getOrNull(),
                hookAfterNavigation = getOrNull(),
            )
        }

        factory<NavigateToUrlUseCase> {
            NavigateToUrlUseCase(
                coroutineScopes = get(),
                useCaseExecutor = get(),
                retrieveMaterialRepository = get(),
                navigationRouteIdGenerator = get(Name.ID_GENERATOR),
                navigationOptionSelectorFactory = get(),
                navigationStackRouteRepository = get(),
                navigationStackScreenRepository = get(),
                navigationStackTransitionRepository = get(),
                shadowerMaterialRepository = get(),
                actionLockRepository = get(),
                hookBeforeNavigation = getOrNull(),
                hookAfterNavigation = getOrNull(),
            )
        }

        factory<NavigationDefinitionSelectorMatcherFactoryUseCase> {
            NavigationDefinitionSelectorMatcherFactoryUseCase()
        }

        factory<NavigationStackTransitionHelperFactoryUseCase> {
            NavigationStackTransitionHelperFactoryUseCase()
        }

        factory<NotifyNavigationTransitionCompletedUseCase> {
            NotifyNavigationTransitionCompletedUseCase(
                navigationAnimatorStackRepository = get()
            )
        }

        factory<ProcessActionUseCase> {
            ProcessActionUseCase(
                coroutineScopes = get(),
                actionProcessors = get(ActionProcessorModule.Name.PROCESSORS)
            )
        }

        factory<RefreshMaterialCacheUseCase> {
            RefreshMaterialCacheUseCase(
                refreshMaterialCacheRepository = get()
            )
        }

        factory<RegisterToScreenTransitionEventUseCase> {
            RegisterToScreenTransitionEventUseCase(
                coroutineScopes = get(),
                navigationAnimatorStackRepository = get()
            )
        }

        factory<RemoveKeyValueFromStoreUseCase> {
            RemoveKeyValueFromStoreUseCase(
                coroutineScopes = get(),
                keyValueRepository = get()
            )
        }

        factory<SaveKeyValueToStoreUseCase> {
            SaveKeyValueToStoreUseCase(
                coroutineScopes = get(),
                keyValueRepository = get()
            )
        }

        factory<UpdateViewUseCase> {
            UpdateViewUseCase(
                coroutineScopes = get(),
                navigationScreenStackRepository = get(),
            )
        }

        factory<SendDataUseCase> {
            SendDataUseCase(
                sendDataAndRetrieveMaterialRepository = get()
            )
        }

    }
}


