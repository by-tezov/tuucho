package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.ComponentRendererProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.StateViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.form.FieldsFormStateViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.form.FormsStateViewProtocol
import com.tezov.tuucho.core.presentation.ui.state.FieldsFormStateView
import com.tezov.tuucho.core.presentation.ui.state.FormsStateView
import com.tezov.tuucho.core.presentation.ui.state.StateView
import com.tezov.tuucho.core.presentation.ui.viewFactory.ButtonViewFactory
import com.tezov.tuucho.core.presentation.ui.viewFactory.ComponentRendererFactory
import com.tezov.tuucho.core.presentation.ui.viewFactory.FieldViewFactory
import com.tezov.tuucho.core.presentation.ui.viewFactory.LabelViewFactory
import com.tezov.tuucho.core.presentation.ui.viewFactory.LayoutLinearViewFactory
import com.tezov.tuucho.core.presentation.ui.viewFactory.SpacerViewFactory
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object MaterialRendererModule {

    internal operator fun invoke() = module {
        state()
        rendered()

        factory<ComponentRendererFactory> {
            ComponentRendererFactory(
                addView = get(),
                viewFactories = listOf(
                    get<LabelViewFactory>(),
                    get<FieldViewFactory>(),
                    get<ButtonViewFactory>(),
                    get<SpacerViewFactory>(),
                    get<LayoutLinearViewFactory>(),
                )
            )
        } bind ComponentRendererProtocol::class
    }

    private fun Module.state() {
        factory<FieldsFormStateViewProtocol> {
            FieldsFormStateView()
        }

        factory<FormsStateViewProtocol> {
            FormsStateView(
                fieldsFormStateView = get()
            )
        }


        single<() -> StateViewProtocol> {
            { StateView(formsStateView = get()) }
        }
    }

    private fun Module.rendered() {
        factory<LayoutLinearViewFactory> { LayoutLinearViewFactory() }

        factory<LabelViewFactory> {
            LabelViewFactory()
        }

        factory<FieldViewFactory> {
            FieldViewFactory(
                useCaseExecutor = get(),
                validatorFactory = get(),
                addForm = get(),
                removeFormFieldView = get(),
                updateFieldFormView = get(),
                isFieldFormViewValid = get()
            )
        }

        factory<ButtonViewFactory> {
            ButtonViewFactory(
                useCaseExecutor = get(),
                actionHandler = get()
            )
        }

        factory<SpacerViewFactory> { SpacerViewFactory() }
    }

}


