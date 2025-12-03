package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.Koin
import org.koin.core.KoinApplication

object KoinContext {
    @TuuchoInternalApi
    lateinit var koinApplication: KoinApplication

    @OptIn(TuuchoInternalApi::class)
    val koin get() = koinApplication.koin
}

interface TuuchoKoinComponent : org.koin.core.component.KoinComponent {
    override fun getKoin(): Koin = KoinContext.koin
}

interface TuuchoKoinScopeComponent :
    TuuchoKoinComponent,
    org.koin.core.component.KoinScopeComponent {

    fun close() {
        scope.close()
    }
}
