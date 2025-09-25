package com.tezov.tuucho.platform.di

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules
import com.tezov.tuucho.core.domain.business.di.SystemCoreDomainModules
import com.tezov.tuucho.core.presentation.ui.di.SystemCoreUiModules
import org.koin.compose.KoinApplication
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module

object StartKoinModules {

    @Composable
    internal operator fun invoke(
        applicationModuleDeclaration: ModuleDeclaration,
        content: @Composable () -> Unit,
    ) = KoinApplication(application = {
        allowOverride(override = false)
        modules(module { applicationModuleDeclaration() })
        modules(SystemCoreDomainModules())
        modules(SystemCoreDataModules())
        modules(SystemCoreUiModules())
        modules(SystemPlatformModules())
    }, content = content)

}
