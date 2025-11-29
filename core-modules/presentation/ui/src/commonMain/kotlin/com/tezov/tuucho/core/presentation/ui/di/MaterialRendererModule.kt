package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import org.koin.core.module.Module
import org.koin.dsl.bind

internal object MaterialRendererModule {
    fun invoke() = module(_root_ide_package_.com.tezov.tuucho.core.presentation.ui.di.ModuleGroupPresentation.Main) {
        viewModule()
        screenModule()
    }

    private fun Module.screenModule() {
        factory<com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenRenderer> {
            _root_ide_package_.com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenRenderer(
                coroutineScopes = get(),
            )
        } bind ScreenRendererProtocol::class

        single<com.tezov.tuucho.core.presentation.ui.renderer.TuuchoEngineProtocol> {
            _root_ide_package_.com.tezov.tuucho.core.presentation.ui.renderer.TuuchoEngine(
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
        factory<List<com.tezov.tuucho.core.presentation.ui.renderer.view._system.AbstractViewFactory>> {
            listOf(
                get<com.tezov.tuucho.core.presentation.ui.renderer.view.LabelViewFactory>(),
                get<com.tezov.tuucho.core.presentation.ui.renderer.view.fieldView.FieldViewFactory>(),
                get<com.tezov.tuucho.core.presentation.ui.renderer.view.ButtonViewFactory>(),
                get<com.tezov.tuucho.core.presentation.ui.renderer.view.SpacerViewFactory>(),
                get<com.tezov.tuucho.core.presentation.ui.renderer.view.LayoutLinearViewFactory>(),
            )
        }

        factory<com.tezov.tuucho.core.presentation.ui.renderer.view.LayoutLinearViewFactory> {
            _root_ide_package_.com.tezov.tuucho.core.presentation.ui.renderer.view
                .LayoutLinearViewFactory()
        }

        factory<com.tezov.tuucho.core.presentation.ui.renderer.view.LabelViewFactory> {
            _root_ide_package_.com.tezov.tuucho.core.presentation.ui.renderer.view
                .LabelViewFactory()
        }

        factory<com.tezov.tuucho.core.presentation.ui.renderer.view.fieldView.FieldViewFactory> {
            _root_ide_package_.com.tezov.tuucho.core.presentation.ui.renderer.view.fieldView.FieldViewFactory(
                useCaseExecutor = get(),
                fieldValidatorFactory = get(),
            )
        }

        factory<com.tezov.tuucho.core.presentation.ui.renderer.view.ButtonViewFactory> {
            _root_ide_package_.com.tezov.tuucho.core.presentation.ui.renderer.view.ButtonViewFactory(
                coroutineScopes = get(),
                useCaseExecutor = get(),
                processAction = get(),
                interactionLockResolver = get()
            )
        }

        factory<com.tezov.tuucho.core.presentation.ui.renderer.view.SpacerViewFactory> {
            _root_ide_package_.com.tezov.tuucho.core.presentation.ui.renderer.view
                .SpacerViewFactory()
        }
    }
}
