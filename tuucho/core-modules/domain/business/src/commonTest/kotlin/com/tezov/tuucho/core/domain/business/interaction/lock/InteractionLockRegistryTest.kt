package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.ActionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InteractionLockRegistryTest {
    private lateinit var sut: InteractionLockRegistry

    @BeforeTest
    fun setup() {
        sut = InteractionLockRegistry()
    }

    private fun action(
        command: String,
        authority: String,
        lockable: InteractionLockable
    ): ActionProtocol = object : ActionProtocol {
        override val command = command
        override val authority = authority
        override val lockable = lockable
    }

    @Test
    fun `register stores a type for given command and authority`() {
        val type = InteractionLockable.Type(listOf(InteractionLockType.Screen))
        sut.register(action("open", "system", type))
        val result = sut.lockTypeFor("open", "system")
        assertEquals(type, result)
    }

    @Test
    fun `register does not store Empty`() {
        sut.register(action("open", "system", InteractionLockable.Empty))
        val result = sut.lockTypeFor("open", "system")
        assertEquals(InteractionLockable.Empty, result)
    }

    @Test
    fun `register throws when key already exists`() {
        val type = InteractionLockable.Type(listOf(InteractionLockType.Screen))
        sut.register(action("open", "system", type))

        val duplicate = InteractionLockable.Type(listOf(InteractionLockType.Navigation))

        assertFailsWith<DomainException.Default> {
            sut.register(action("open", "system", duplicate))
        }
    }

    @Test
    fun `register throws when lockable is not Type or Empty`() {
        val invalid = InteractionLockable.Lock(emptyList())
        assertFailsWith<DomainException.Default> {
            sut.register(action("open", "system", invalid))
        }
    }

    @Test
    fun `lockTypeFor returns Empty when key is not found`() {
        val result = sut.lockTypeFor("missing", "none")
        assertEquals(InteractionLockable.Empty, result)
    }

    @Test
    fun `lockTypeFor returns different values for different command-authority pairs`() {
        val a = InteractionLockable.Type(listOf(InteractionLockType.Screen))
        val b = InteractionLockable.Type(listOf(InteractionLockType.Navigation))
        sut.register(action("open", "system", a))
        sut.register(action("open", "settings", b))

        val resultA = sut.lockTypeFor("open", "system")
        val resultB = sut.lockTypeFor("open", "settings")

        assertEquals(a, resultA)
        assertEquals(b, resultB)
    }
}
