package com.tezov.tuucho.core.ui.di

import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FieldsMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.form.FormMaterialStateProtocol
import com.tezov.tuucho.core.ui.state.FieldsMaterialState
import com.tezov.tuucho.core.ui.state.FormMaterialState
import com.tezov.tuucho.core.ui.state.MaterialState
import com.tezov.tuucho.core.ui.uiComponentFactory.ButtonUiComponentFactory
import com.tezov.tuucho.core.ui.uiComponentFactory.FieldUiComponentFactory
import com.tezov.tuucho.core.ui.uiComponentFactory.LabelUiComponentFactory
import com.tezov.tuucho.core.ui.uiComponentFactory.LayoutLinearUiComponentFactory
import com.tezov.tuucho.core.ui.uiComponentFactory.MaterialUiComponentFactory
import com.tezov.tuucho.core.ui.uiComponentFactory.SpacerUiComponentFactory
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object MaterialRendererModule {

    internal operator fun invoke() = module {
        state()
        rendered()

        single<MaterialUiComponentFactory> {
            MaterialUiComponentFactory(
                addScreenInMaterialState = get(),
                uiComponentFactory = listOf(
                    get<LabelUiComponentFactory>(),
                    get<FieldUiComponentFactory>(),
                    get<ButtonUiComponentFactory>(),
                    get<SpacerUiComponentFactory>(),
                    get<LayoutLinearUiComponentFactory>(),
                )
            )
        } bind ScreenRendererProtocol::class
    }

    private fun Module.state() {
        single<FieldsMaterialStateProtocol> {
            FieldsMaterialState()
        }

        single<FormMaterialStateProtocol> {
            FormMaterialState(
                fieldsMaterialState = get()
            )
        }

        single<MaterialStateProtocol> {
            MaterialState(
                formMaterialState = get()
            )
        }
    }

    private fun Module.rendered() {
        single<LayoutLinearUiComponentFactory> { LayoutLinearUiComponentFactory() }

        single<LabelUiComponentFactory> {
            LabelUiComponentFactory()
        }

        single<FieldUiComponentFactory> {
            FieldUiComponentFactory(
                validatorFactory = get(),
                addFormInMaterialState = get(),
                clearFormInMaterialState = get(),
                updateFieldForm = get(),
                isFieldFormValid = get()
            )
        }

        single<ButtonUiComponentFactory> {
            ButtonUiComponentFactory(
                actionHandler = get()
            )
        }

        single<SpacerUiComponentFactory> { SpacerUiComponentFactory() }
    }

}


