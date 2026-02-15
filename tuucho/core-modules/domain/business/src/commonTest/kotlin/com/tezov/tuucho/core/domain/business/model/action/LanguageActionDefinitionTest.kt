package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageActionDefinitionTest {
    @Test
    fun `current command is language`() {
        assertEquals("language", LanguageActionDefinition.Current.command)
    }

    @Test
    fun `current authority is current`() {
        assertEquals("current", LanguageActionDefinition.Current.authority)
    }

    @Test
    fun `current lockable is empty`() {
        assertEquals(
            listOf(
                InteractionLockType.Screen,
                InteractionLockType.Navigation
            ),
            LanguageActionDefinition.Current.lockable.getTypes()
        )
    }

    @Test
    fun `current query code is correct`() {
        assertEquals("code", LanguageActionDefinition.Current.Query.code)
    }

    @Test
    fun `current query country is correct`() {
        assertEquals("country", LanguageActionDefinition.Current.Query.country)
    }

    @Test
    fun `system command is language`() {
        assertEquals("language", LanguageActionDefinition.System.command)
    }

    @Test
    fun `system authority is system`() {
        assertEquals("system", LanguageActionDefinition.System.authority)
    }

    @Test
    fun `system lockable is empty`() {
        assertEquals(
            listOf(
                InteractionLockType.Screen,
                InteractionLockType.Navigation
            ),
            LanguageActionDefinition.System.lockable.getTypes()
        )
    }
}
