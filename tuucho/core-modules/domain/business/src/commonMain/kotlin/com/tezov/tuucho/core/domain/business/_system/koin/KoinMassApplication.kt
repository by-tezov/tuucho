@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.dsl.onClose

@TuuchoInternalApi
fun koinApplication(
    koinMassModules: List<KoinMass>,
    extension: (KoinApplication.() -> Unit)?
): KoinApplication = koinApplication {
    allowOverride(override = false)
}.apply {
    modules(module {
        singleOf(::KoinIsolatedContextLifeCycle) onClose { lifeCycle -> lifeCycle?.onClose() }
    })
    koin.get<KoinIsolatedContextLifeCycle>().init(this)
    modules(koinMassModules.groupBy { it.group }.map { (_, groups) ->
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
}
