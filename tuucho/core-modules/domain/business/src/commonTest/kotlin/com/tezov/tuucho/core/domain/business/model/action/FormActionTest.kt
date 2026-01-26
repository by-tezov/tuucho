package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FormActionTest {
    @Test
    fun `send command is form`() {
        assertEquals("form", FormActionDefinition.Send.command)
    }

    @Test
    fun `send authority is send-url`() {
        assertEquals("send-url", FormActionDefinition.Send.authority)
    }

    @Test
    fun `send lockable combines navigate url and local destination`() {
        val combined = FormActionDefinition.Send.lockable
        assertTrue(combined.getTypes().containsAll(NavigateActionDefinition.Url.lockable.getTypes()))
        assertTrue(combined.getTypes().containsAll(NavigateActionDefinition.LocalDestination.lockable.getTypes()))
    }

    @Test
    fun `update command is form`() {
        assertEquals("form", FormActionDefinition.Update.command)
    }

    @Test
    fun `update authority is update`() {
        assertEquals("update", FormActionDefinition.Update.authority)
    }

    @Test
    fun `update lockable is empty`() {
        assertSame(InteractionLockable.Empty, FormActionDefinition.Update.lockable)
    }

    @Test
    fun `update target error is correct`() {
        assertEquals("error", FormActionDefinition.Update.Target.error)
    }
}
