package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase.Input
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SaveKeyValueToStoreUseCaseTest {
    private lateinit var keyValueRepository: KeyValueStoreRepositoryProtocol

    private lateinit var sut: SaveKeyValueToStoreUseCase

    @BeforeTest
    fun setup() {
        keyValueRepository = mock()
        sut = SaveKeyValueToStoreUseCase(
            keyValueRepository = keyValueRepository
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(keyValueRepository)
    }

    @Test
    fun `invoke saves key and non null value`() = runTest {
        val key = "username".toKey()
        val value = "john".toValue()

        val input = Input(
            key = key,
            value = value
        )

        everySuspend { keyValueRepository.save(any(), any()) } returns Unit

        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            keyValueRepository.save(key, value)
        }
    }

    @Test
    fun `invoke saves key with null value`() = runTest {
        val key = "token".toKey()
        val value = null

        val input = Input(
            key = key,
            value = value
        )

        everySuspend { keyValueRepository.save(any(), any()) } returns Unit

        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            keyValueRepository.save(key, value)
        }
    }
}
