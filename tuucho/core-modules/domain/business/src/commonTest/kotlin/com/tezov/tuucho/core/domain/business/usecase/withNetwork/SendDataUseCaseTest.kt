package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.mock.middlewareWithReturn.MockMiddlewareExecutorWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SendDataUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var middlewareExecutor: MiddlewareExecutorProtocolWithReturn

    private lateinit var sendDataAndRetrieveMaterialRepository: MaterialRepositoryProtocol.SendDataAndRetrieve
    private lateinit var sendDataMiddlewares: List<SendDataMiddleware>

    private lateinit var sut: SendDataUseCase

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        middlewareExecutor = MockMiddlewareExecutorWithReturn()
        sendDataAndRetrieveMaterialRepository = mock()
        sendDataMiddlewares = listOf()
        sut = SendDataUseCase(
            coroutineScopes = coroutineTestScope.mock,
            sendDataAndRetrieveMaterialRepository = sendDataAndRetrieveMaterialRepository,
            middlewareExecutor = middlewareExecutor,
            sendDataMiddlewares = sendDataMiddlewares
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(
            sendDataAndRetrieveMaterialRepository
        )
    }

    @Test
    fun `invoke executes middlewares then terminal repository call`() = coroutineTestScope.run {
        val urlValue = "https://example.com/post"
        val requestJson = JsonObject(emptyMap())
        val responseJson = JsonObject(mapOf("result" to JsonNull))

        val input = SendDataUseCase.Input(
            url = urlValue,
            jsonObject = requestJson
        )

        val expectedOutput = SendDataUseCase.Output(jsonObject = responseJson)

        everySuspend {
            sendDataAndRetrieveMaterialRepository.process(any(), any())
        } returns responseJson

        val result = sut.invoke(input)

        assertEquals(expectedOutput, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.io.withContext<Any>(any())
            coroutineTestScope.mock.io.dispatcher
            sendDataAndRetrieveMaterialRepository.process(urlValue, requestJson)
        }
    }
}
