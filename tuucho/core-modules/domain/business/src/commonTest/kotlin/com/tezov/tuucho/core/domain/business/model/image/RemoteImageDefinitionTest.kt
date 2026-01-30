package com.tezov.tuucho.core.domain.business.model.image

import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteImageDefinitionTest {
    @Test
    fun `command is remote`() {
        assertEquals("remote", RemoteImageDefinition.command)
    }
}
