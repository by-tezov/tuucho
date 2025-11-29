package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@TuuchoInternalApi
object SystemCoreUiModules {
    fun invoke(): List<ModuleProtocol> = listOf(
        _root_ide_package_.com.tezov.tuucho.core.presentation.ui.di.MaterialRendererModule
            .invoke(),
    )
}
