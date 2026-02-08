package com.tezov.tuucho.core.domain.business.model.action

import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

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
        assertSame(InteractionLockable.Empty, LanguageActionDefinition.Current.lockable)
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
        assertSame(InteractionLockable.Empty, LanguageActionDefinition.System.lockable)
    }
}
