package com.tezov.tuucho.core.ui.di

import com.tezov.tuucho.core.domain.protocol.ComponentRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.ScreenStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FieldsFormStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FormsStateProtocol
import com.tezov.tuucho.core.ui.state.FieldsFormState
import com.tezov.tuucho.core.ui.state.FormsState
import com.tezov.tuucho.core.ui.state.ScreenState
import com.tezov.tuucho.core.ui.viewFactory.ButtonViewFactory
import com.tezov.tuucho.core.ui.viewFactory.ComponentRendererFactory
import com.tezov.tuucho.core.ui.viewFactory.FieldViewFactory
import com.tezov.tuucho.core.ui.viewFactory.LabelViewFactory
import com.tezov.tuucho.core.ui.viewFactory.LayoutLinearViewFactory
import com.tezov.tuucho.core.ui.viewFactory.SpacerViewFactory
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
                uiComponentFactory = listOf(
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
        factory<FieldsFormStateProtocol> {
            FieldsFormState()
        }

        factory<FormsStateProtocol> {
            FormsState(
                fieldsFormState = get()
            )
        }

        //TODO: when navigation stack will be done, it must not be a single but a factory
        single <ScreenStateProtocol> {
            ScreenState(
                formsState = get()
            )
        }
    }

    private fun Module.rendered() {
        factory<LayoutLinearViewFactory> { LayoutLinearViewFactory() }

        factory<LabelViewFactory> {
            LabelViewFactory()
        }

        factory<FieldViewFactory> {
            FieldViewFactory(
                validatorFactory = get(),
                addForm = get(),
                removeFormFieldView = get(),
                updateFieldFormView = get(),
                isFieldFormViewValid = get()
            )
        }

        factory<ButtonViewFactory> {
            ButtonViewFactory(
                actionHandler = get()
            )
        }

        factory <SpacerViewFactory> { SpacerViewFactory() }
    }

}


