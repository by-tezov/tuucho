package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.ActionDefinitionProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InteractionLockRegistryTest {

    private fun action(
        command: String,
        authority: String,
        lockable: InteractionLockable
    ): ActionDefinitionProtocol = object : ActionDefinitionProtocol {
        override val command = command
        override val authority = authority
        override val lockable = lockable
    }

    @Test
    fun `stores a type for given command and authority`() {
        val type = InteractionLockable.Type(listOf(InteractionLockType.Screen))

        val sut = InteractionLockRegistry(
            listOf(action("open", "system", type))
        )

        assertEquals(type, sut.lockTypeFor("open", "system"))
    }

    @Test
    fun `does not store Empty`() {
        val sut = InteractionLockRegistry(
            listOf(action("open", "system", InteractionLockable.Empty))
        )

        assertEquals(
            InteractionLockable.Empty,
            sut.lockTypeFor("open", "system")
        )
    }

    @Test
    fun `last definition wins when same key is registered twice`() {
        val first = InteractionLockable.Type(listOf(InteractionLockType.Screen))
        val second = InteractionLockable.Type(listOf(InteractionLockType.Navigation))

        val sut = InteractionLockRegistry(
            listOf(
                action("open", "system", first),
                action("open", "system", second)
            )
        )

        assertEquals(second, sut.lockTypeFor("open", "system"))
    }

    @Test
    fun `throws when lockable is not Type or Empty`() {
        val invalid = InteractionLockable.Lock(emptyList())

        assertFailsWith<DomainException.Default> {
            InteractionLockRegistry(
                listOf(action("open", "system", invalid))
            )
        }
    }

    @Test
    fun `returns Empty when key is not found`() {
        val sut = InteractionLockRegistry(emptyList())

        assertEquals(
            InteractionLockable.Empty,
            sut.lockTypeFor("missing", "none")
        )
    }

    @Test
    fun `returns different values for different command-authority pairs`() {
        val a = InteractionLockable.Type(listOf(InteractionLockType.Screen))
        val b = InteractionLockable.Type(listOf(InteractionLockType.Navigation))

        val sut = InteractionLockRegistry(
            listOf(
                action("open", "system", a),
                action("open", "settings", b)
            )
        )

        assertEquals(a, sut.lockTypeFor("open", "system"))
        assertEquals(b, sut.lockTypeFor("open", "settings"))
    }
}
