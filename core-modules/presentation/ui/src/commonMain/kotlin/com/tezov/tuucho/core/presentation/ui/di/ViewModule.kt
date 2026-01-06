package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.presentation.ui.view.ButtonViewFactory
import com.tezov.tuucho.core.presentation.ui.view.FieldViewFactory
import com.tezov.tuucho.core.presentation.ui.view.LabelViewFactory
import com.tezov.tuucho.core.presentation.ui.view.LayoutLinearViewFactory
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object ViewModule {
    fun invoke() = module(ModuleGroupPresentation.View) {
        factoryOf(::LayoutLinearViewFactory) bind ViewFactoryProtocol::class
        factoryOf(::LabelViewFactory) bind ViewFactoryProtocol::class
        factoryOf(::FieldViewFactory) bind ViewFactoryProtocol::class
        factoryOf(::ButtonViewFactory) bind ViewFactoryProtocol::class
    }
}
