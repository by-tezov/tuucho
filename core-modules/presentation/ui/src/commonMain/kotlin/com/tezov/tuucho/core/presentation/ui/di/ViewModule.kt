package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.presentation.ui.view.ButtonViewFactory
import com.tezov.tuucho.core.presentation.ui.view.FieldViewFactory
import com.tezov.tuucho.core.presentation.ui.view.LabelViewFactory
import com.tezov.tuucho.core.presentation.ui.view.LayoutLinearViewFactory
import com.tezov.tuucho.core.presentation.ui.view.SpacerViewFactory
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import org.koin.dsl.bind

internal object ViewModule {
    fun invoke() = module(ModuleGroupPresentation.View) {
        factory<LayoutLinearViewFactory> {
            LayoutLinearViewFactory()
        } bind ViewFactoryProtocol::class

        factory<LabelViewFactory> {
            LabelViewFactory()
        } bind ViewFactoryProtocol::class

        factory<FieldViewFactory> {
            FieldViewFactory()
        } bind ViewFactoryProtocol::class

        factory<ButtonViewFactory> {
            ButtonViewFactory()
        } bind ViewFactoryProtocol::class

        factory<SpacerViewFactory> {
            SpacerViewFactory()
        } bind ViewFactoryProtocol::class
    }
}
