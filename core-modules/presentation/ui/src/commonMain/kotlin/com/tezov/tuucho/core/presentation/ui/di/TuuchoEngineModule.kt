package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.render.TuuchoEngine
import com.tezov.tuucho.core.presentation.ui.render.TuuchoEngineProtocol
import com.tezov.tuucho.core.presentation.ui.render.misc.RendererIdGenerator
import com.tezov.tuucho.core.presentation.ui.screen.ScreenFactory
import org.koin.dsl.bind

internal object TuuchoEngineModule {
    fun invoke() = module(ModuleGroupPresentation.Main) {
        factory<RendererIdGenerator> {
            RendererIdGenerator(
                idGenerator = get(),
            )
        }

        factory<ScreenFactory> {
            ScreenFactory(
                coroutineScopes = get(),
            )
        } bind ScreenFactoryProtocol::class

        single<TuuchoEngineProtocol> {
            TuuchoEngine(
                coroutineScopes = get(),
                useCaseExecutor = get(),
                processAction = get(),
                registerToScreenTransitionEvent = get(),
                notifyNavigationTransitionCompleted = get(),
                getScreensFromRoutes = get(),
            )
        }
    }
}
