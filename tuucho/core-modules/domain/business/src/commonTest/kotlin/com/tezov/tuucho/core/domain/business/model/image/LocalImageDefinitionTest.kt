package com.tezov.tuucho.core.domain.business.model.image

import kotlin.test.Test
import kotlin.test.assertEquals

class LocalImageDefinitionTest {
    @Test
    fun `command is local`() {
        assertEquals("local", LocalImageDefinition.command)
    }
}
