package com.tezov.tuucho.core.barrel.di

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules
import com.tezov.tuucho.core.domain.business.di.KoinContext
import com.tezov.tuucho.core.domain.business.di.SystemCoreDomainModules
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import com.tezov.tuucho.core.presentation.ui.di.SystemCoreUiModules
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal expect fun SystemCoreModules.platformInvoke(): List<ModuleProtocol>

internal object SystemCoreModules {
    fun invoke(): List<ModuleProtocol> = listOf(
        CoroutineScopeModules.invoke(),
    ) +
        platformInvoke()

    @OptIn(TuuchoInternalApi::class)
    @Composable
    fun remember(
        modules: List<ModuleProtocol>,
    ) = androidx.compose.runtime.remember {
        koinApplication {
            allowOverride(override = false)
            val map = listOf(
                modules,
                SystemCoreDomainModules.invoke(),
                SystemCoreDataModules.invoke(),
                SystemCoreUiModules.invoke(),
                SystemCoreModules.invoke()
            ).flatten().groupBy { it.group }
            modules(map.map { (_, modules) ->
                module {
                    modules.forEach { module ->
                        module.run { declaration() }
                    }
                }
            })
        }.also { KoinContext.koinApplication = it }
    }
}
