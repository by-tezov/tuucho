package com.tezov.tuucho.sample.uiExtension.di

import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module
import com.tezov.tuucho.core.presentation.ui.di.ModuleContextPresentation
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.sample.uiExtension.presentation.CustomLabelViewFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object ViewModule {
    fun invoke() = module(ModuleContextPresentation.View) {
        factoryOf(::CustomLabelViewFactory) bind ViewFactoryProtocol::class
    }
}
