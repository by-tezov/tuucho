package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.getAllOrdered
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessImageUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveLocalImageUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveRemoteImageUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ServerHealthCheckUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.FormValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetLanguageUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreenOrNullUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreensFromRoutesUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.HasKeyInStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigateFinishUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationDefinitionSelectorMatcherFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationStackTransitionHelperFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NotifyNavigationTransitionCompletedUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RegisterToScreenTransitionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RemoveKeyValueFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal object UseCaseModule {
    fun invoke() = module(ModuleContextDomain.UseCase) {
        singleOf(::UseCaseExecutor) bind UseCaseExecutorProtocol::class
        withNetworkModule()
        withoutNetworkModule()
    }

    private fun Module.withNetworkModule() {
        factory {
            NavigateBackUseCase(
                coroutineScopes = get(),
                useCaseExecutor = get(),
                navigationStackRouteRepository = get(),
                navigationStackScreenRepository = get(),
                navigationStackTransitionRepository = get(),
                retrieveMaterialRepository = get(),
                shadowerMaterialRepository = get(),
                middlewareExecutor = get(),
                navigationMiddlewares = getAllOrdered(),
                navigateFinish = get()
            )
        }

        factory {
            NavigateToUrlUseCase(
                coroutineScopes = get(),
                useCaseExecutor = get(),
                retrieveMaterialRepository = get(),
                navigationRouteIdGenerator = get(),
                navigationOptionSelectorFactory = get(),
                navigationStackRouteRepository = get(),
                navigationStackScreenRepository = get(),
                navigationStackTransitionRepository = get(),
                shadowerMaterialRepository = get(),
                middlewareExecutor = get(),
                navigationMiddlewares = getAllOrdered()
            )
        }

        factoryOf(::ProcessActionUseCase)
        factoryOf(::ProcessImageUseCase)
        factoryOf(::RefreshMaterialCacheUseCase)
        factoryOf(::RetrieveLocalImageUseCase)
        factoryOf(::RetrieveRemoteImageUseCase)
        factoryOf(::ServerHealthCheckUseCase)

        factory<SendDataUseCase> {
            SendDataUseCase(
                sendDataAndRetrieveMaterialRepository = get(),
                middlewareExecutor = get(),
                sendDataMiddlewares = getAllOrdered()
            )
        }
    }

    private fun Module.withoutNetworkModule() {
        factoryOf(::FormValidatorFactoryUseCase)
        factoryOf(::GetLanguageUseCase)
        factoryOf(::GetScreenOrNullUseCase)
        factoryOf(::GetScreensFromRoutesUseCase)
        factoryOf(::GetValueOrNullFromStoreUseCase)
        factoryOf(::HasKeyInStoreUseCase)
        factory {
            NavigateFinishUseCase(
                middlewareExecutor = get(),
                navigationMiddlewares = getAllOrdered()
            )
        }
        factoryOf(::NavigationDefinitionSelectorMatcherFactoryUseCase)
        factoryOf(::NavigationStackTransitionHelperFactoryUseCase)
        factoryOf(::NotifyNavigationTransitionCompletedUseCase)
        factoryOf(::RegisterToScreenTransitionEventUseCase)
        factoryOf(::RemoveKeyValueFromStoreUseCase)
        factoryOf(::SaveKeyValueToStoreUseCase)

        factory<UpdateViewUseCase> {
            UpdateViewUseCase(
                navigationScreenStackRepository = get(),
                middlewareExecutor = get(),
                updateViewMiddlewares = getAllOrdered()
            )
        }
    }
}
