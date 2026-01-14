@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import org.koin.core.annotation.KoinInternalApi
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstantTest {
    @OptIn(KoinInternalApi::class)
    @Test
    fun `getAllOrdered - no ordered bindings returns empty list`() {
        val koin = koinApplication {}.koin
        assertEquals(Constant.koinRootScopeQualifier, koin.scopeRegistry.rootScope.scopeQualifier)
    }
}
