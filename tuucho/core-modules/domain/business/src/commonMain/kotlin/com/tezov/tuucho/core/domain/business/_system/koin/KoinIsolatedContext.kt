package com.tezov.tuucho.core.domain.business._system.koin

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

object KoinIsolatedContext {
    @TuuchoInternalApi
    var koinApplication: KoinApplication? = null

    @OptIn(TuuchoInternalApi::class)
    val koin get() = koinApplication?.koin ?: throw DomainException.Default("KoinIsolatedContextLifeCycle not initialized")
}

interface TuuchoKoinComponent : KoinComponent {
    override fun getKoin(): Koin = KoinIsolatedContext.koin
}

interface TuuchoKoinScopeComponent : TuuchoKoinComponent, KoinScopeComponent {

    @TuuchoInternalApi
    override val scope: Scope get() = lazyScope.value

    val lazyScope: Lazy<Scope>

    fun isScopeInitialized() = lazyScope.isInitialized()

    fun isScopeClosed() = lazyScope.value.closed

    fun closeScope() {
        if(isScopeInitialized() && !isScopeClosed()) {
            lazyScope.value.close()
        }
    }
}

@OptIn(TuuchoInternalApi::class)
class KoinIsolatedContextLifeCycle {

    fun init(koinApplication: KoinApplication) {
        KoinIsolatedContext.koinApplication = koinApplication
    }

    fun onClose() {
        KoinIsolatedContext.koinApplication = null
    }
}
