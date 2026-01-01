package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@TuuchoInternalApi
object SystemCoreUiModules {
    fun invoke(): List<ModuleProtocol> = listOf(
        TuuchoEngineModule.invoke(),
        ViewModule.invoke(),
    )
}
