package com.tezov.tuucho.core.presentation.ui.preview

import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

object DummyKoin {
    operator fun invoke(
        module: Module
    ) = GlobalContext.getOrNull() ?: startKoin {}.koin.also {
        it.loadModules(listOf(module))
    }
}
