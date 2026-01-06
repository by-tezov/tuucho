package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.render.TuuchoEngine
import com.tezov.tuucho.core.presentation.ui.render.TuuchoEngineProtocol
import com.tezov.tuucho.core.presentation.ui.render.misc.RendererIdGenerator
import com.tezov.tuucho.core.presentation.ui.screen.ScreenFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal object TuuchoEngineModule {
    fun invoke() = module(ModuleGroupPresentation.Main) {
        factoryOf(::RendererIdGenerator)

        factoryOf(::ScreenFactory) bind ScreenFactoryProtocol::class

        singleOf(::TuuchoEngine) bind TuuchoEngineProtocol::class
    }
}
