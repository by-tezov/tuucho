package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import com.tezov.tuucho.core.presentation.ui.renderer._system.IdGenerator
import com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenIdentifier
import com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenRenderer
import com.tezov.tuucho.core.presentation.ui.renderer.screen._system.ScreenIdentifierFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view.ButtonViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view.LabelViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view.LayoutLinearViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view.SpacerViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewIdentifier
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewIdentifierFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view.fieldView.FieldViewFactory
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object MaterialRendererModule {

    internal operator fun invoke() = module {

        single<IdGenerator> { IdGenerator() }

        viewModule()
        screenModule()
    }

    private fun Module.screenModule() {
        factory<ScreenIdentifierFactory> {
            {
                ScreenIdentifier(
                    idGenerator = get()
                )
            }
        }

        factory<ScreenRenderer> {
            ScreenRenderer(
                identifierFactory = get()
            )
        } bind ScreenRendererProtocol::class
    }

    private fun Module.viewModule() {

        factory<ViewIdentifierFactory> {
            { screenIdentifier ->
                ViewIdentifier(
                    idGenerator = get(),
                    screenIdentifier = screenIdentifier,
                )
            }
        }

        factory<List<ViewFactory>> {
            listOf(
                get<LabelViewFactory>(),
                get<FieldViewFactory>(),
                get<ButtonViewFactory>(),
                get<SpacerViewFactory>(),
                get<LayoutLinearViewFactory>(),
            )
        }

        factory<LayoutLinearViewFactory> {
            LayoutLinearViewFactory(
                identifierFactory = get(),
            )
        }

        factory<LabelViewFactory> {
            LabelViewFactory(
                identifierFactory = get(),
            )
        }

        factory<FieldViewFactory> {
            FieldViewFactory(
                identifierFactory = get(),
                useCaseExecutor = get(),
                validatorFactory = get(),
            )
        }

        factory<ButtonViewFactory> {
            ButtonViewFactory(
                identifierFactory = get(),
                useCaseExecutor = get(),
                actionHandler = get()
            )
        }

        factory<SpacerViewFactory> {
            SpacerViewFactory(
                identifierFactory = get()
            )
        }
    }

}


