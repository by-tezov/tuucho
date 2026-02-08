package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SetLanguageUseCase.Input
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

class SetLanguageUseCaseTest {
    private lateinit var systemPlatformRepository: SystemPlatformRepositoryProtocol

    private lateinit var sut: SetLanguageUseCase

    @BeforeTest
    fun setup() {
        systemPlatformRepository = mock()
        sut = SetLanguageUseCase(
            platformRepository = systemPlatformRepository
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(systemPlatformRepository)
    }

    @Test
    fun `invoke with language`() = runTest {
        val language = LanguageModelDomain(
            code = "en",
            country = "EN"
        )

        val input = Input(
            language = language,
        )

        everySuspend { systemPlatformRepository.setCurrentLanguage(any()) } returns Unit

        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            systemPlatformRepository.setCurrentLanguage(language)
        }
    }
}
