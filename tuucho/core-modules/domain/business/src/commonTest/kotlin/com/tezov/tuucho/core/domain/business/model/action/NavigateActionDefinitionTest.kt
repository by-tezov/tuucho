package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.model.action.NavigateActionDefinition.LocalDestination
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigateActionDefinitionTest {
    @Test
    fun `url command is navigate`() {
        assertEquals("navigate", NavigateActionDefinition.Url.command)
    }

    @Test
    fun `url authority is url`() {
        assertEquals("url", NavigateActionDefinition.Url.authority)
    }

    @Test
    fun `send lockable combines navigate url and local destination`() {
        assertEquals(
            listOf(
                InteractionLockType.Screen,
                InteractionLockType.Navigation
            ),
            NavigateActionDefinition.Url.lockable.getTypes()
        )
    }

    @Test
    fun `local command is navigate`() {
        assertEquals("navigate", LocalDestination.command)
    }

    @Test
    fun `local authority is local-destination`() {
        assertEquals("local-destination", LocalDestination.authority)
    }

    @Test
    fun `local lockable contains screen and navigation`() {
        assertEquals(
            listOf(
                InteractionLockType.Screen,
                InteractionLockType.Navigation
            ),
            NavigateActionDefinition.Url.lockable.getTypes()
        )
    }

    @Test
    fun `local target values are correct`() {
        assertEquals("back", LocalDestination.Target.back)
        assertEquals("finish", LocalDestination.Target.finish)
        assertEquals("current", LocalDestination.Target.current)
    }
}
