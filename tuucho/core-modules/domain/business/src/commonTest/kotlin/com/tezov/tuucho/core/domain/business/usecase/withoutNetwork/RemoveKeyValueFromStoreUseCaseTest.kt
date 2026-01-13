package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RemoveKeyValueFromStoreUseCase.Input
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

class RemoveKeyValueFromStoreUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()

    private lateinit var keyValueRepository: KeyValueStoreRepositoryProtocol

    private lateinit var sut: RemoveKeyValueFromStoreUseCase

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        keyValueRepository = mock()
        sut = RemoveKeyValueFromStoreUseCase(
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
    fun `invoke removes key by saving null value`() = coroutineTestScope.run {
        val key = "session_token".toKey()

        val input = Input(
            key = key
        )

        everySuspend { keyValueRepository.save(any(), any()) } returns Unit

        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.io.await<Any>(any())
            keyValueRepository.save(key, null)
        }
    }
}
