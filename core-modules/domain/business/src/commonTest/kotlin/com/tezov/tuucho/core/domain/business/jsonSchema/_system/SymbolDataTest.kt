@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.jsonSchema._system

import kotlin.test.Test
import kotlin.test.assertEquals

class SymbolDataTest {
    @Test
    fun `ID_REF_INDICATOR has expected value`() {
        assertEquals("*", SymbolData.ID_REF_INDICATOR)
    }

    @Test
    fun `ID_GROUP_SEPARATOR has expected value`() {
        assertEquals("@", SymbolData.ID_GROUP_SEPARATOR)
    }
}
