package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.di.NavigationModule.Name
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ServerHealthCheckUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.FormValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetLanguageUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreenOrNullUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreensFromRoutesUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.HasKeyInStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationDefinitionSelectorMatcherFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationStackTransitionHelperFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NotifyNavigationTransitionCompletedUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RegisterToScreenTransitionEventUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RemoveKeyValueFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase
import com.tezov.tuucho.core.domain.tool.di.ExtensionKoin.getAllOrdered
import org.koin.core.module.Module

internal object UseCaseModule {
    fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupDomain.UseCase

        override fun Module.declaration() {
            single<UseCaseExecutor> {
                UseCaseExecutor(
                    coroutineScopes = get()
                )
            }
            withNetworkModule()
            withoutNetworkModule()
        }

        private fun Module.withNetworkModule() {
            factory<NavigateBackUseCase> {
                NavigateBackUseCase(
                    coroutineScopes = get(),
                    navigationStackRouteRepository = get(),
                    navigationStackScreenRepository = get(),
                    navigationStackTransitionRepository = get(),
                    shadowerMaterialRepository = get(),
                    actionLockRepository = get(),
                    navigationMiddlewares = getKoin().getAllOrdered()
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
                    navigationMiddlewares = getKoin().getAllOrdered()
                )
            }

            factory<ProcessActionUseCase> {
                ProcessActionUseCase(
                    coroutineScopes = get(),
                    actionProcessors = getAll<ActionProcessorProtocol>()
                )
            }

            factory<RefreshMaterialCacheUseCase> {
                RefreshMaterialCacheUseCase(
                    refreshMaterialCacheRepository = get()
                )
            }

            factory<ServerHealthCheckUseCase> {
                ServerHealthCheckUseCase(
                    coroutineScopes = get(),
                    serverHealthCheck = get(),
                )
            }

            factory<SendDataUseCase> {
                SendDataUseCase(
                    sendDataAndRetrieveMaterialRepository = get()
                )
            }
        }

        private fun Module.withoutNetworkModule() {
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

            factory<GetValueFromStoreUseCase> {
                GetValueFromStoreUseCase(
                    coroutineScopes = get(),
                    keyValueRepository = get()
                )
            }

            factory<GetValueOrNullFromStoreUseCase> {
                GetValueOrNullFromStoreUseCase(
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
        }
    }
}
