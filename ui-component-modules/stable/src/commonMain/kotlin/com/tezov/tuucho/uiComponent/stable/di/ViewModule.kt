package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.presentation.ui.di.ModuleGroupPresentation
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.uiComponent.stable.presentation.view.ButtonViewFactory
import com.tezov.tuucho.uiComponent.stable.presentation.view.FieldViewFactory
import com.tezov.tuucho.uiComponent.stable.presentation.view.LabelViewFactory
import com.tezov.tuucho.uiComponent.stable.presentation.view.LayoutLinearViewFactory
import com.tezov.tuucho.uiComponent.stable.presentation.view.SpacerViewFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object ViewModule {
    fun invoke() = module(ModuleGroupPresentation.View) {
        factoryOf(::SpacerViewFactory) bind ViewFactoryProtocol::class
        factoryOf(::LayoutLinearViewFactory) bind ViewFactoryProtocol::class
        factoryOf(::LabelViewFactory) bind ViewFactoryProtocol::class
        factoryOf(::FieldViewFactory) bind ViewFactoryProtocol::class
        factoryOf(::ButtonViewFactory) bind ViewFactoryProtocol::class
    }
}
