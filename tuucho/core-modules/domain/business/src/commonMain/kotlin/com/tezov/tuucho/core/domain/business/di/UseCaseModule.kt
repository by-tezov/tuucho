package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.getAllOrdered
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateShadowerUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase
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
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.ResolveLanguageValueUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SetLanguageUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.TransformImageJsonArrayToImageModelUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

internal object UseCaseModule {
    fun invoke() = module(ModuleContextDomain.UseCase) {
        single<UseCaseExecutor>() bind UseCaseExecutorProtocol::class
        withNetworkModule()
        withoutNetworkModule()
    }

    private fun Module.withNetworkModule() {
        factory {
            NavigateBackUseCase(
                useCaseExecutor = get(),
                navigationStackRouteRepository = get(),
                navigationStackScreenRepository = get(),
                navigationStackTransitionRepository = get(),
                middlewareExecutor = get(),
                navigationMiddlewares = getAllOrdered(),
                navigateFinish = get(),
                navigateShadower = get()
            )
        }

        factory<NavigateShadowerUseCase>()

        factory {
            NavigateToUrlUseCase(
                useCaseExecutor = get(),
                navigationRouteIdGenerator = get(),
                navigationStackRouteRepository = get(),
                navigationStackScreenRepository = get(),
                navigationStackTransitionRepository = get(),
                middlewareExecutor = get(),
                navigationMiddlewares = getAllOrdered(),
                navigateShadower = get()
            )
        }

        factory<ProcessActionUseCase>()
        factory<RefreshMaterialCacheUseCase>()
        factory {
            RetrieveImageUseCase<Any>(
                coroutineScopes = get(),
                imageRepository = get(),
                middlewareExecutor = get(),
                retrieveImageMiddlewares = getAllOrdered()
            )
        }
        factory<ServerHealthCheckUseCase>()

        factory<SendDataUseCase> {
            SendDataUseCase(
                coroutineScopes = get(),
                sendDataAndRetrieveMaterialRepository = get(),
                middlewareExecutor = get(),
                sendDataMiddlewares = getAllOrdered()
            )
        }
    }

    private fun Module.withoutNetworkModule() {
        factory<FormValidatorFactoryUseCase>()
        factory<GetLanguageUseCase>()
        factory<GetScreenOrNullUseCase>()
        factory<GetScreensFromRoutesUseCase>()
        factory<GetValueOrNullFromStoreUseCase>()
        factory<HasKeyInStoreUseCase>()
        factory {
            NavigateFinishUseCase(
                middlewareExecutor = get(),
                navigationMiddlewares = getAllOrdered()
            )
        }
        factory<NavigationDefinitionSelectorMatcherFactoryUseCase>()
        factory<NavigationStackTransitionHelperFactoryUseCase>()
        factory<NotifyNavigationTransitionCompletedUseCase>()
        factory<RegisterToScreenTransitionEventUseCase>()
        factory<RemoveKeyValueFromStoreUseCase>()
        factory<ResolveLanguageValueUseCase>()
        factory<SaveKeyValueToStoreUseCase>()
        factory<SetLanguageUseCase>()
        factory<TransformImageJsonArrayToImageModelUseCase>()
        factory<UpdateViewUseCase> {
            UpdateViewUseCase(
                navigationScreenStackRepository = get(),
                middlewareExecutor = get(),
                updateViewMiddlewares = getAllOrdered()
            )
        }
    }
}
