package com.tezov.tuucho.sample.uiExtension.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.presentation.ui.di.ModuleContextPresentation
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.sample.uiExtension.presentation.CustomLabelViewFactory
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory

internal object ViewModule {
    fun invoke() = module(ModuleContextPresentation.View) {
        factory<CustomLabelViewFactory>() bind ViewFactoryProtocol::class
    }
}
