package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
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

class RefreshMaterialCacheUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()

    private lateinit var refreshMaterialCacheRepository: MaterialRepositoryProtocol.RefreshCache
    private lateinit var sut: RefreshMaterialCacheUseCase

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        refreshMaterialCacheRepository = mock()
        sut = RefreshMaterialCacheUseCase(
            refreshMaterialCacheRepository = refreshMaterialCacheRepository
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(refreshMaterialCacheRepository)
    }

    @Test
    fun `invoke forwards url to repository`() = coroutineTestScope.run {
        val urlValue = "https://example.com/material.json"
        val input = RefreshMaterialCacheUseCase.Input(url = urlValue)

        everySuspend { refreshMaterialCacheRepository.process(any()) } returns Unit

        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            refreshMaterialCacheRepository.process(urlValue)
        }
    }
}
