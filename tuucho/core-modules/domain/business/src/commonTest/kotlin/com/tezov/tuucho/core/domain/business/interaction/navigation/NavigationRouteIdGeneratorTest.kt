package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationRouteIdGeneratorTest {
    private lateinit var generator: IdGeneratorProtocol<Unit, String>
    private lateinit var sut: NavigationRouteIdGenerator

    @BeforeTest
    fun setup() {
        generator = mock()
        sut = NavigationRouteIdGenerator(generator)
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(generator)
    }

    @Test
    fun `generate delegates to underlying generator`() {
        every { generator.generate() } returns "id_123"

        val result = sut.generate()

        assertEquals("id_123", result)

        verify(VerifyMode.exhaustiveOrder) {
            generator.generate()
        }
    }
}
