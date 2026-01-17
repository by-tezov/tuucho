package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.ServerHealthCheckRepositoryProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerHealthCheckUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()

    private lateinit var serverHealthCheck: ServerHealthCheckRepositoryProtocol
    private lateinit var sut: ServerHealthCheckUseCase

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        serverHealthCheck = mock()
        sut = ServerHealthCheckUseCase(
            serverHealthCheck = serverHealthCheck
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(serverHealthCheck)
    }

    @Test
    fun `invoke extracts health status from json response`() = coroutineTestScope.run {
        val urlValue = "https://example.com/health"

        val responseJson = JsonObject(
            mapOf(
                "health" to JsonPrimitive("ok")
            )
        )

        everySuspend { serverHealthCheck.process(any()) } returns responseJson

        val input = ServerHealthCheckUseCase.Input(url = urlValue)

        val result = sut.invoke(input)

        assertEquals("ok", result.status)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            serverHealthCheck.process(urlValue)
        }
    }
}
