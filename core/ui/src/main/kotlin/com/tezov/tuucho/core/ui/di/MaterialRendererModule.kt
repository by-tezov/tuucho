package com.tezov.tuucho.core.ui.di

import com.tezov.tuucho.core.ui.renderer.ButtonRendered
import com.tezov.tuucho.core.ui.renderer.LabelRendered
import com.tezov.tuucho.core.ui.renderer.LayoutLinearRendered
import com.tezov.tuucho.core.ui.renderer.MaterialRenderer
import com.tezov.tuucho.core.ui.renderer.SpacerRendered
import org.koin.dsl.module

object MaterialRendererModule {

    internal operator fun invoke() = module {
        single<MaterialRenderer> {
            MaterialRenderer(listOf(
                LayoutLinearRendered(),
                LabelRendered(),
                ButtonRendered(),
                SpacerRendered(),
            ))
        }
    }

}


