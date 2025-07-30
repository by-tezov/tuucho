package com.tezov.tuucho.core.ui.di

import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.FieldsMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.FormMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterUpdateFormEventUseCase
import com.tezov.tuucho.core.domain.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.ui.composable.ButtonUiFactory
import com.tezov.tuucho.core.ui.composable.FieldRendered
import com.tezov.tuucho.core.ui.composable.LabelRendered
import com.tezov.tuucho.core.ui.composable.LayoutLinearRendered
import com.tezov.tuucho.core.ui.composable.MaterialUiComponentFactory
import com.tezov.tuucho.core.ui.composable.SpacerRendered
import com.tezov.tuucho.core.ui.state.FieldsMaterialState
import com.tezov.tuucho.core.ui.state.FormMaterialState
import com.tezov.tuucho.core.ui.state.MaterialState
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object MaterialRendererModule {

    internal operator fun invoke() = module {
        state()
        rendered()

        single<MaterialUiComponentFactory> {
            MaterialUiComponentFactory(
                listOf(
                    get<LabelRendered>(),
                    get<FieldRendered>(),
                    get<ButtonUiFactory>(),
                    get<SpacerRendered>(),
                    get<LayoutLinearRendered>(),
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
                get<FieldsMaterialStateProtocol>()
            )
        }

        single<MaterialStateProtocol> {
            MaterialState(
                get<FormMaterialStateProtocol>()
            )
        }
    }

    private fun Module.rendered() {
        single<LayoutLinearRendered> { LayoutLinearRendered() }

        single<LabelRendered> {
            LabelRendered(
                materialState = get<MaterialStateProtocol>()
            )
        }

        single<FieldRendered> {
            FieldRendered(
                get<MaterialStateProtocol>(),
                get<ValidatorFactoryUseCase>(),
                get<RegisterUpdateFormEventUseCase>(),
                get<GetLanguageUseCase>()
            )
        }

        single<ButtonUiFactory> { ButtonUiFactory() }

        single<SpacerRendered> { SpacerRendered() }
    }

}


