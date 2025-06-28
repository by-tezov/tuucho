package com.tezov.tuucho.core.ui.di

import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.ui.renderer.ButtonRendered
import com.tezov.tuucho.core.ui.renderer.ComponentRenderer
import com.tezov.tuucho.core.ui.renderer.LabelRendered
import com.tezov.tuucho.core.ui.renderer.LayoutLinearRendered
import com.tezov.tuucho.core.ui.renderer.SpacerRendered
import org.koin.dsl.module

object MaterialRendererModule {

    internal operator fun invoke() = module {

        single<ComponentRenderer> {
            ComponentRenderer(listOf(
                LayoutLinearRendered(),
                LabelRendered(),
                ButtonRendered(),
                SpacerRendered(),
            ))
        }

        single<ScreenRendererProtocol> {
            get<ComponentRenderer>()
        }
    }

}


