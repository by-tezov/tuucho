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
import kotlin.collections.List
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.flatten
import kotlin.collections.forEach
import kotlin.collections.groupBy
import kotlin.collections.listOf
import kotlin.collections.map
import kotlin.collections.plus

internal expect fun SystemCoreModules.platformInvoke(): List<ModuleProtocol>

internal object SystemCoreModules {
    fun invoke(): List<ModuleProtocol> = listOf(
        CoroutineScopeModules.invoke(),
    ) +
        platformInvoke()

    @OptIn(TuuchoInternalApi::class)
    @Composable
    fun remember(
        applicationModules: List<ModuleProtocol>,
    ) = androidx.compose.runtime.remember {
        koinApplication {
            allowOverride(override = false)
            val map = listOf(
                SystemCoreDomainModules.invoke(),
                SystemCoreDataModules.invoke(),
                SystemCoreUiModules.invoke(),
                invoke(),
                applicationModules,
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
