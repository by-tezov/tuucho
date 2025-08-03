package com.tezov.tuucho.kmm.di

import com.tezov.tuucho.core.data.di.SystemCoreDataModules
import com.tezov.tuucho.core.domain.di.SystemCoreDomainModules
import com.tezov.tuucho.core.presentation.ui.di.SystemCoreUiModules
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

object StartKoinModules {

    internal operator fun invoke(
        platformModulesDeclaration: KoinAppDeclaration = {}
    ) = startKoin {
        allowOverride(override = false)
        platformModulesDeclaration()
        modules(SystemCoreDomainModules())
        modules(SystemCoreDataModules())
        modules(SystemCoreUiModules())
        modules(SystemKmmModules())
    }

}
