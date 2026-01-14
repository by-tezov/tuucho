package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.KoinScopeComponent

object KoinIsolatedContext {
    @TuuchoInternalApi
    lateinit var koinApplication: KoinApplication

    @OptIn(TuuchoInternalApi::class)
    val koin get() = koinApplication.koin
}

interface TuuchoKoinComponent : KoinComponent {
    override fun getKoin(): Koin = KoinIsolatedContext.koin
}

interface TuuchoKoinScopeComponent :
    TuuchoKoinComponent,
    KoinScopeComponent {
    fun close() {
        scope.close()
    }
}
