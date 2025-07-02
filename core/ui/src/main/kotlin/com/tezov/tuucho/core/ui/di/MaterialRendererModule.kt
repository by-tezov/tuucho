package com.tezov.tuucho.core.ui.di

import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.FieldsMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.FormMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.ui.renderer.ButtonRendered
import com.tezov.tuucho.core.ui.renderer.ComponentRenderer
import com.tezov.tuucho.core.ui.renderer.FieldRendered
import com.tezov.tuucho.core.ui.renderer.LabelRendered
import com.tezov.tuucho.core.ui.renderer.LayoutLinearRendered
import com.tezov.tuucho.core.ui.renderer.SpacerRendered
import com.tezov.tuucho.core.ui.state.FieldsMaterialState
import com.tezov.tuucho.core.ui.state.FormMaterialState
import com.tezov.tuucho.core.ui.state.MaterialState
import org.koin.dsl.module

object MaterialRendererModule {

    internal operator fun invoke() = module {

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

        single<ScreenRendererProtocol> {
            ComponentRenderer(listOf(
                LayoutLinearRendered(),
                LabelRendered(),
                FieldRendered(get<MaterialStateProtocol>()),
                ButtonRendered(),
                SpacerRendered(),
            ))
        }
    }

}


