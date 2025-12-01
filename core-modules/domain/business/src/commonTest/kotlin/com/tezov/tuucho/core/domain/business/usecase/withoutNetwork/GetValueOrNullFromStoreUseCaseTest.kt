package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase.Output
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

class GetValueOrNullFromStoreUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()

    private lateinit var keyValueRepository: KeyValueStoreRepositoryProtocol

    private lateinit var sut: GetValueOrNullFromStoreUseCase

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        keyValueRepository = mock()
        sut = GetValueOrNullFromStoreUseCase(
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
    fun `invoke returns value when present`() = coroutineTestScope.run {
        val key = "session".toKey()
        val value = "token-123".toValue()

        val input = Input(
            key = key
        )

        everySuspend { keyValueRepository.getOrNull(any()) } returns value

        val result = sut.invoke(input)

        assertEquals(
            Output(value = value),
            result
        )

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.io.await<Any>(any())
            keyValueRepository.getOrNull(key)
        }
    }

    @Test
    fun `invoke returns null when value is missing`() = coroutineTestScope.run {
        val key = "missing_key".toKey()

        val input = Input(
            key = key
        )

        everySuspend { keyValueRepository.getOrNull(any()) } returns null

        val result = sut.invoke(input)

        assertEquals(
            Output(value = null),
            result
        )

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.io.await<Any>(any())
            keyValueRepository.getOrNull(key)
        }
    }
}
