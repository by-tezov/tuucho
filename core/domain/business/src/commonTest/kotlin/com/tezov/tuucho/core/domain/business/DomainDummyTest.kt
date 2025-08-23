package com.tezov.tuucho.core.domain.business

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals


interface MyRepository {
    suspend fun getData(): String
}

class DomainDummyTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun mockk_poc() = runTest {
        val repository = mock<MyRepository> {
            everySuspend { getData() } returns "Hello Mokkery"
        }

        val result = repository.getData()

        assertEquals("Hello Mokkery", result)
        verifySuspend { repository.getData() }
    }
}