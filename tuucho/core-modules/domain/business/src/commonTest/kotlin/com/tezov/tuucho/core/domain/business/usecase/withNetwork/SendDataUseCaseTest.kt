package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware
import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware.Context
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SendDataUseCaseTest {
    private lateinit var sendDataAndRetrieveMaterialRepository: MaterialRepositoryProtocol.SendDataAndRetrieve
    private lateinit var middlewareExecutor: MiddlewareExecutorProtocol
    private lateinit var sendDataMiddlewares: List<SendDataMiddleware>

    private lateinit var sut: SendDataUseCase

    @BeforeTest
    fun setup() {
        sendDataAndRetrieveMaterialRepository = mock()
        middlewareExecutor = mock()
        sendDataMiddlewares = listOf()
        sut = SendDataUseCase(
            sendDataAndRetrieveMaterialRepository = sendDataAndRetrieveMaterialRepository,
            middlewareExecutor = middlewareExecutor,
            sendDataMiddlewares = sendDataMiddlewares
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            sendDataAndRetrieveMaterialRepository,
            middlewareExecutor
        )
    }

    @Test
    fun `invoke executes middlewares then terminal repository call`() = runTest {
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

        everySuspend {
            middlewareExecutor.process<Context, SendDataUseCase.Output>(any(), any())
        } calls { args ->
            val middlewares = args.arg<List<SendDataMiddleware>>(0)
            val context = args.arg<Context>(1)
            middlewares[0].process(context, null)
        }

        val result = sut.invoke(input)

        assertEquals(expectedOutput, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            middlewareExecutor.process<Context, SendDataUseCase.Output>(any(), any())
            sendDataAndRetrieveMaterialRepository.process(urlValue, requestJson)
        }
    }
}
