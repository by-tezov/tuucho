package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.domain.business.protocol.HookProtocol
import com.tezov.tuucho.shared.sample.hooks.BeforeNavigateToUrlHook
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

object HookModule {

    fun invoke(): ModuleDeclaration = {

        factory<BeforeNavigateToUrlHook> {
            BeforeNavigateToUrlHook(
                useCaseExecutor = get(),
                serverHealthCheck = get(),
                refreshMaterialCache = get(),
                getValueOrNullFromStore = get(),
                navigateToUrl = get(),
            )
        } bind HookProtocol.BeforeNavigateToUrl::class
    }
}