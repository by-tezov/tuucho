package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.interaction.action.FormSendUrlAction
import com.tezov.tuucho.core.domain.business.interaction.action.FormUpdateAction
import com.tezov.tuucho.core.domain.business.interaction.action.NavigationLocalDestinationAction
import com.tezov.tuucho.core.domain.business.interaction.action.NavigationUrlAction
import com.tezov.tuucho.core.domain.business.interaction.action.StoreAction
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import org.koin.dsl.bind

internal object ActionProcessorModule {
    fun invoke() = module(ModuleGroupDomain.Middleware) {
        factory<FormSendUrlAction> {
            FormSendUrlAction(
                useCaseExecutor = get(),
                getOrNullScreen = get(),
                sendData = get()
            )
        } bind ActionMiddleware::class

        factory<FormUpdateAction> {
            FormUpdateAction(
                useCaseExecutor = get(),
                updateView = get()
            )
        } bind ActionMiddleware::class

        factory<NavigationLocalDestinationAction> {
            NavigationLocalDestinationAction(
                useCaseExecutor = get(),
                navigateBack = get()
            )
        } bind ActionMiddleware::class

        factory<NavigationUrlAction> {
            NavigationUrlAction(
                useCaseExecutor = get(),
                navigateToUrl = get()
            )
        } bind ActionMiddleware::class

        factory<StoreAction> {
            StoreAction(
                useCaseExecutor = get(),
                saveKeyValueToStore = get(),
                removeKeyValueFromStore = get()
            )
        } bind ActionMiddleware::class
    }
}
