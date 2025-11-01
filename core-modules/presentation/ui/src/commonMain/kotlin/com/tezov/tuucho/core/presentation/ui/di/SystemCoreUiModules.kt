package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

object SystemCoreUiModules {
    fun invoke(): List<ModuleProtocol> = listOf(
        MaterialRendererModule.invoke(),
    )
}
