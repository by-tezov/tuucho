package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FormActionTest {
    @Test
    fun `send command is form`() {
        assertEquals("form", FormAction.Send.command)
    }

    @Test
    fun `send authority is send-url`() {
        assertEquals("send-url", FormAction.Send.authority)
    }

    @Test
    fun `send lockable combines navigate url and local destination`() {
        val combined = FormAction.Send.lockable
        assertTrue(combined.getTypes().containsAll(NavigateAction.Url.lockable.getTypes()))
        assertTrue(combined.getTypes().containsAll(NavigateAction.LocalDestination.lockable.getTypes()))
    }

    @Test
    fun `send action labels are correct`() {
        assertEquals("validated", FormAction.Send.ActionLabel.validated)
        assertEquals("denied", FormAction.Send.ActionLabel.denied)
    }

    @Test
    fun `update command is form`() {
        assertEquals("form", FormAction.Update.command)
    }

    @Test
    fun `update authority is update`() {
        assertEquals("update", FormAction.Update.authority)
    }

    @Test
    fun `update lockable is empty`() {
        assertSame(InteractionLockable.Empty, FormAction.Update.lockable)
    }

    @Test
    fun `update target error is correct`() {
        assertEquals("error", FormAction.Update.Target.error)
    }
}
