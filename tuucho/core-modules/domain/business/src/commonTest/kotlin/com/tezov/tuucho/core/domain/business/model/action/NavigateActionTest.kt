package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigateActionTest {
    @Test
    fun `url command is navigate`() {
        assertEquals("navigate", NavigateAction.Url.command)
    }

    @Test
    fun `url authority is url`() {
        assertEquals("url", NavigateAction.Url.authority)
    }

    @Test
    fun `send lockable combines navigate url and local destination`() {
        assertEquals(
            listOf(
                InteractionLockType.Screen,
                InteractionLockType.Navigation
            ),
            NavigateAction.Url.lockable.getTypes()
        )
    }

    @Test
    fun `local command is navigate`() {
        assertEquals("navigate", NavigateAction.LocalDestination.command)
    }

    @Test
    fun `local authority is local-destination`() {
        assertEquals("local-destination", NavigateAction.LocalDestination.authority)
    }

    @Test
    fun `local lockable contains screen and navigation`() {
        assertEquals(
            listOf(
                InteractionLockType.Screen,
                InteractionLockType.Navigation
            ),
            NavigateAction.Url.lockable.getTypes()
        )
    }

    @Test
    fun `local target values are correct`() {
        assertEquals("back", NavigateAction.LocalDestination.Target.back)
        assertEquals("finish", NavigateAction.LocalDestination.Target.finish)
    }
}
