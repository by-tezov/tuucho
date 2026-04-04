package com.tezov.tuucho.core.barrel.di

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.data.repository.di.SystemCoreDataModules
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business._system.koin.koinApplication
import com.tezov.tuucho.core.domain.business.di.SystemCoreDomainModules
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import com.tezov.tuucho.core.presentation.ui.di.SystemCoreUiModules
import org.koin.core.KoinApplication

internal expect fun SystemCoreModules.platformInvoke(): List<KoinMass>

internal object SystemCoreModules {
    fun invoke(): List<KoinMass> = listOf(
        CoroutineScopeModules.invoke(),
    ) + platformInvoke()

    @OptIn(TuuchoInternalApi::class)
    @Composable
    fun remember(
        koinMassModules: List<KoinMass>,
        extension: (KoinApplication.() -> Unit)?
    ) = androidx.compose.runtime.remember {
        koinApplication(
            koinMassModules = SystemCoreDomainModules.invoke() +
                SystemCoreDataModules.invoke() +
                SystemCoreUiModules.invoke() +
                invoke() +
                koinMassModules,
            extension = extension
        )
    }
}
