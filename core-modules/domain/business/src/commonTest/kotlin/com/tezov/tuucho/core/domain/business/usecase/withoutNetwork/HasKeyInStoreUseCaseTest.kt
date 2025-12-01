package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.HasKeyInStoreUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.HasKeyInStoreUseCase.Output
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HasKeyInStoreUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()

    private lateinit var keyValueRepository: KeyValueStoreRepositoryProtocol

    private lateinit var sut: HasKeyInStoreUseCase

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        keyValueRepository = mock()
        sut = HasKeyInStoreUseCase(
            coroutineScopes = coroutineTestScope.mock,
            keyValueRepository = keyValueRepository
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(keyValueRepository)
    }

    @Test
    fun `invoke returns true when key exists`() = coroutineTestScope.run {
        val key = "user_token".toKey()
        val input = Input(key = key)

        everySuspend { keyValueRepository.hasKey(any()) } returns true

        val result = sut.invoke(input)

        assertEquals(
            Output(result = true),
            result
        )

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.io.await<Any>(any())
            keyValueRepository.hasKey(key)
        }
    }

    @Test
    fun `invoke returns false when key does not exist`() = coroutineTestScope.run {
        val key = "missing".toKey()
        val input = Input(key = key)

        everySuspend { keyValueRepository.hasKey(any()) } returns false

        val result = sut.invoke(input)

        assertEquals(
            Output(result = false),
            result
        )

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.io.await<Any>(any())
            keyValueRepository.hasKey(key)
        }
    }
}
