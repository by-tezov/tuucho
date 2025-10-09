package com.tezov.tuucho.core.domain.business.model

import com.tezov.tuucho.core.domain.business.exception.DomainException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ActionModelDomainTest {

    @Test
    fun `from parses full string with command authority and target`() {
        val action = ActionModelDomain.Companion.from("open://system/settings")
        assertEquals("open", action.command)
        assertEquals("system", action.authority)
        assertEquals("settings", action.target)
    }

    @Test
    fun `from parses string with command and authority only`() {
        val action = ActionModelDomain.Companion.from("open://system")
        assertEquals("open", action.command)
        assertEquals("system", action.authority)
        assertNull(action.target)
    }

    @Test
    fun `from parses string with command only`() {
        val action = ActionModelDomain.Companion.from("open://")

        println(action)

        assertEquals("open", action.command)
        assertNull(action.authority)
        assertNull(action.target)
    }

    @Test
    fun `from throws when command is missing`() {
        assertFailsWith<DomainException.Default> {
            ActionModelDomain.Companion.from("://system/settings")
        }
    }

    @Test
    fun `from factory method with explicit fields`() {
        val action = ActionModelDomain.Companion.from(
            command = "edit",
            authority = "file",
            target = "document.txt"
        )
        assertEquals("edit", action.command)
        assertEquals("file", action.authority)
        assertEquals("document.txt", action.target)
    }

    @Test
    fun `target can contain nested path`() {
        val action = ActionModelDomain.Companion.from("open://system/config/network/wifi")
        assertEquals("system", action.authority)
        assertEquals("config/network/wifi", action.target)
    }
}
