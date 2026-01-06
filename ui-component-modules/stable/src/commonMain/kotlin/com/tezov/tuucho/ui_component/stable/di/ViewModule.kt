package com.tezov.tuucho.ui_component.stable.di

import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.presentation.ui.di.ModuleGroupPresentation
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.ui_component.stable.presentation.view.SpacerViewFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object ViewModule {
    fun invoke() = module(ModuleGroupPresentation.View) {
        factoryOf(::SpacerViewFactory) bind ViewFactoryProtocol::class
    }
}
