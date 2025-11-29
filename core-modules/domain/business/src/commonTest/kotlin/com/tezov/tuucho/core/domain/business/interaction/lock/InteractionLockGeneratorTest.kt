package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifyNoMoreCalls
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class InteractionLockGeneratorTest {
    private lateinit var idGenerator: IdGeneratorProtocol<Unit, String>
    private lateinit var sut: InteractionLockGenerator

    @BeforeTest
    fun setup() {
        idGenerator = mock()
        sut = InteractionLockGenerator(idGenerator)
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(idGenerator)
    }

    @Test
    fun `generate input returns interaction lock with fields`() {
        val owner = "owner"
        val type = InteractionLockType.Screen
        val id = "generated-id"

        every { idGenerator.generate() } returns id

        val result = sut.generate(
            InteractionLockGenerator.Input(
                owner = owner,
                type = type
            )
        )

        assertEquals(owner, result.owner)
        assertEquals(type, result.type)
        assertEquals(id, result.id)
        assertSame(result.id, id)

        verify {
            idGenerator.generate()
        }
    }

    @Test
    fun `generate without input throws`() {
        assertFailsWith<DomainException.Default> {
            sut.generate()
        }
        verify(exactly(0)) {
            idGenerator.generate()
        }
    }
}
