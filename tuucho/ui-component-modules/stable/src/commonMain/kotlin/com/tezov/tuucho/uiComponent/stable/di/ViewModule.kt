package com.tezov.tuucho.uiComponent.stable.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.presentation.ui.di.ModuleContextPresentation
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.uiComponent.stable.presentation.view.ButtonViewFactory
import com.tezov.tuucho.uiComponent.stable.presentation.view.FieldViewFactory
import com.tezov.tuucho.uiComponent.stable.presentation.view.ImageViewFactory
import com.tezov.tuucho.uiComponent.stable.presentation.view.LabelViewFactory
import com.tezov.tuucho.uiComponent.stable.presentation.view.LayoutLinearViewFactory
import com.tezov.tuucho.uiComponent.stable.presentation.view.SpacerViewFactory
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

internal object ViewModule {
    fun invoke() = module(ModuleContextPresentation.View) {
        factory<SpacerViewFactory>() bind ViewFactoryProtocol::class
        factory<LayoutLinearViewFactory>() bind ViewFactoryProtocol::class
        factory<LabelViewFactory>() bind ViewFactoryProtocol::class
        factory<FieldViewFactory>() bind ViewFactoryProtocol::class
        factory<ButtonViewFactory>() bind ViewFactoryProtocol::class
        factory<ImageViewFactory>() bind ViewFactoryProtocol::class
    }
}
