package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetTextUseCase.Input
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetTextUseCaseTest {
    private lateinit var systemPlatformRepository: SystemPlatformRepositoryProtocol
    private lateinit var sut: GetTextUseCase

    @BeforeTest
    fun setup() {
        systemPlatformRepository = mock()
        sut = GetTextUseCase(
            platformRepository = systemPlatformRepository
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(systemPlatformRepository)
    }

    @Test
    fun `invoke returns text matching full language tag`() = runTest {
        val language = LanguageModelDomain(
            code = "en",
            country = "US"
        )

        val json = buildJsonObject {
            put("en-US", "hello")
            put("en", "fallback")
        }

        everySuspend { systemPlatformRepository.getCurrentLanguage() } returns language

        val output = sut.invoke(Input(json))

        assertEquals("hello", output.text)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            systemPlatformRepository.getCurrentLanguage()
        }
    }

    @Test
    fun `invoke returns text matching language code`() = runTest {
        val language = LanguageModelDomain(
            code = "en",
            country = null
        )

        val json = buildJsonObject {
            put("en", "hello")
            put("fr", "bonjour")
        }

        everySuspend { systemPlatformRepository.getCurrentLanguage() } returns language

        val output = sut.invoke(Input(json))

        assertEquals("hello", output.text)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            systemPlatformRepository.getCurrentLanguage()
        }
    }

    @Test
    fun `invoke returns text matching code fallback`() = runTest {
        val language = LanguageModelDomain(
            code = "en",
            country = "GB"
        )

        val json = buildJsonObject {
            put("en-US", "hello-us")
            put("en-CA", "hello-ca")
        }

        everySuspend { systemPlatformRepository.getCurrentLanguage() } returns language

        val output = sut.invoke(Input(json))

        assertEquals("hello-us", output.text)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            systemPlatformRepository.getCurrentLanguage()
        }
    }

    @Test
    fun `invoke returns default text when no language matches`() = runTest {
        val language = LanguageModelDomain(
            code = "de",
            country = "DE"
        )

        val json = buildJsonObject {
            put("default", "default-text")
            put("en", "hello")
        }

        everySuspend { systemPlatformRepository.getCurrentLanguage() } returns language

        val output = sut.invoke(Input(json))

        assertEquals("default-text", output.text)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            systemPlatformRepository.getCurrentLanguage()
        }
    }

    @Test
    fun `invoke returns null when nothing matches`() = runTest {
        val language = LanguageModelDomain(
            code = "de",
            country = "DE"
        )

        val json = buildJsonObject {
            put("en", "hello")
        }

        everySuspend { systemPlatformRepository.getCurrentLanguage() } returns language

        val output = sut.invoke(Input(json))

        assertNull(output.text)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            systemPlatformRepository.getCurrentLanguage()
        }
    }
}
