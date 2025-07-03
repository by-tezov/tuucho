package com.tezov.tuucho.core.ui.di

import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.FieldsMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.FormMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterUpdateFormEventUseCase
import com.tezov.tuucho.core.domain.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.ui.renderer.ButtonRendered
import com.tezov.tuucho.core.ui.renderer.ComponentRenderer
import com.tezov.tuucho.core.ui.renderer.FieldRendered
import com.tezov.tuucho.core.ui.renderer.LabelRendered
import com.tezov.tuucho.core.ui.renderer.LayoutLinearRendered
import com.tezov.tuucho.core.ui.renderer.SpacerRendered
import com.tezov.tuucho.core.ui.state.FieldsMaterialState
import com.tezov.tuucho.core.ui.state.FormMaterialState
import com.tezov.tuucho.core.ui.state.MaterialState
import org.koin.core.module.Module
import org.koin.dsl.module

object MaterialRendererModule {

    internal operator fun invoke() = module {
        state()
        rendered()

        single<ScreenRendererProtocol> {
            ComponentRenderer(
                listOf(
                    get<LabelRendered>(),
                    get<FieldRendered>(),
                    get<ButtonRendered>(),
                    get<SpacerRendered>(),
                    get<LayoutLinearRendered>(),
                )
            )
        }
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
        single<LabelRendered> { LabelRendered(
            get<GetLanguageUseCase>()
        ) }
        single<FieldRendered> {
            FieldRendered(
                get<MaterialStateProtocol>(),
                get<ValidatorFactoryUseCase>(),
                get<RegisterUpdateFormEventUseCase>(),
                get<GetLanguageUseCase>()
            )
        }
        single<ButtonRendered> { ButtonRendered() }
        single<SpacerRendered> { SpacerRendered() }
    }

}


