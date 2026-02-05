package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.render.TuuchoEngine
import com.tezov.tuucho.core.presentation.ui.render.TuuchoEngineProtocol
import com.tezov.tuucho.core.presentation.ui.render.misc.RendererIdGenerator
import com.tezov.tuucho.core.presentation.ui.screen.ScreenFactory
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

internal object TuuchoEngineModule {
    fun invoke() = module(ModuleContextPresentation.Main) {
        factory<RendererIdGenerator>()
        factory<ScreenFactory>() bind ScreenFactoryProtocol::class
        single<TuuchoEngine>() bind TuuchoEngineProtocol::class
    }
}
