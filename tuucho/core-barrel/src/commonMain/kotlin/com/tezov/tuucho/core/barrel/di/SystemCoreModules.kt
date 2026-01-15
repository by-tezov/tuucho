package com.tezov.tuucho.core.barrel.di

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules
import com.tezov.tuucho.core.domain.business._system.koin.KoinIsolatedContext
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business.di.SystemCoreDomainModules
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import com.tezov.tuucho.core.presentation.ui.di.SystemCoreUiModules
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal expect fun SystemCoreModules.platformInvoke(): List<KoinMass>

internal object SystemCoreModules {
    fun invoke(): List<KoinMass> = listOf(
        CoroutineScopeModules.invoke(),
    ) +
        platformInvoke()

    @OptIn(TuuchoInternalApi::class)
    @Composable
    fun remember(
        modules: List<KoinMass>,
        extension: (KoinApplication.() -> Unit)?
    ) = androidx.compose.runtime.remember {
        val koins = SystemCoreDomainModules.invoke() +
            SystemCoreDataModules.invoke() +
            SystemCoreUiModules.invoke() +
            invoke() +
            modules
        koinApplication {
            allowOverride(override = false)
            modules(koins.groupBy { it.group }.map { (_, groups) ->
                val (modules, scopes) = groups.partition { it is KoinMass.Module }
                module {
                    @Suppress("UNCHECKED_CAST")
                    (modules as List<KoinMass.Module>).forEach { module ->
                        module.declaration(this)
                    }
                    @Suppress("UNCHECKED_CAST")
                    (scopes as List<KoinMass.Scope>)
                        .groupBy { it.scopeContext }
                        .forEach { (scopeContext, koinScopes) ->
                            scope(scopeContext) {
                                koinScopes.forEach { it.declaration(this) }
                            }
                        }
                }
            })
            extension?.invoke(this)
        }.also { KoinIsolatedContext.koinApplication = it }
    }
}
