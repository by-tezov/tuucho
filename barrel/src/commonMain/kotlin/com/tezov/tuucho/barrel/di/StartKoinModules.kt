package com.tezov.tuucho.barrel.di

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules
import com.tezov.tuucho.core.domain.business.di.SystemCoreDomainModules
import com.tezov.tuucho.core.presentation.ui.di.SystemCoreUiModules
import org.koin.compose.KoinApplication
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module

object StartKoinModules {

    @Composable
    operator fun invoke(
        moduleDeclarations: List<ModuleDeclaration>,
        content: @Composable () -> Unit,
    ) = KoinApplication(application = {
        allowOverride(override = false)
        moduleDeclarations.forEach {
            modules(module { it() })
        }
        modules(SystemCoreDomainModules.invoke())
        modules(SystemCoreDataModules.invoke())
        modules(SystemCoreUiModules.invoke())
        modules(SystemBarrelModules.invoke())
    }, content = content)

}
