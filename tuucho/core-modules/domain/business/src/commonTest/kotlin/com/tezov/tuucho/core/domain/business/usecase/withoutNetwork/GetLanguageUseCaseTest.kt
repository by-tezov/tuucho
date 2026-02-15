package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetLanguageUseCaseTest {
    private lateinit var systemPlatformRepository: SystemPlatformRepositoryProtocol

    private lateinit var sut: GetLanguageUseCase

    @BeforeTest
    fun setup() {
        systemPlatformRepository = mock()
        sut = GetLanguageUseCase(
            platformRepository = systemPlatformRepository
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(systemPlatformRepository)
    }

    @Test
    fun `invoke return current language`() = runTest {
        val language = LanguageModelDomain(
            code = "fr",
            country = "CA"
        )

        everySuspend { systemPlatformRepository.getCurrentLanguage() } returns language

        val result = sut.invoke(Unit)

        assertEquals(language, result.language)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            systemPlatformRepository.getCurrentLanguage()
        }
    }
}
