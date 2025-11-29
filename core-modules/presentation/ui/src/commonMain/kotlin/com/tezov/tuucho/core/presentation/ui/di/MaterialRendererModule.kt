package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import com.tezov.tuucho.core.presentation.ui.renderer.TuuchoEngine
import com.tezov.tuucho.core.presentation.ui.renderer.TuuchoEngineProtocol
import com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenRenderer
import com.tezov.tuucho.core.presentation.ui.renderer.view.ButtonViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view.LabelViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view.LayoutLinearViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view.SpacerViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.AbstractViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view.fieldView.FieldViewFactory
import org.koin.core.module.Module
import org.koin.dsl.bind

internal object MaterialRendererModule {
    fun invoke() = module(ModuleGroupPresentation.Main) {
        viewModule()
        screenModule()
    }

    private fun Module.screenModule() {
        factory<ScreenRenderer> {
            ScreenRenderer(
                coroutineScopes = get(),
            )
        } bind ScreenRendererProtocol::class

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

    private fun Module.viewModule() {
        factory<List<AbstractViewFactory>> {
            listOf(
                get<LabelViewFactory>(),
                get<FieldViewFactory>(),
                get<ButtonViewFactory>(),
                get<SpacerViewFactory>(),
                get<LayoutLinearViewFactory>(),
            )
        }

        factory<LayoutLinearViewFactory> {
            LayoutLinearViewFactory()
        }

        factory<LabelViewFactory> {
           LabelViewFactory()
        }

        factory<FieldViewFactory> {
            FieldViewFactory(
                useCaseExecutor = get(),
                fieldValidatorFactory = get(),
            )
        }

        factory<ButtonViewFactory> {
            ButtonViewFactory(
                coroutineScopes = get(),
                useCaseExecutor = get(),
                processAction = get(),
                interactionLockResolver = get()
            )
        }

        factory<SpacerViewFactory> {
            SpacerViewFactory()
        }
    }
}
