package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.mock.CoroutineTestScope
import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.FindAllRefOrNullFetcherProtocol
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.data.repository.repository.source.SendDataAndRetrieveMaterialRemoteSource
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SendDataAndRetrieveMaterialRemoteSourceTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var networkJsonObject: NetworkJsonObject
    private lateinit var responseRectifier: ResponseRectifier
    private lateinit var responseAssembler: ResponseAssembler
    private lateinit var materialDatabaseSource: MaterialDatabaseSource

    private lateinit var sut: SendDataAndRetrieveMaterialRemoteSource

    @BeforeTest
    fun setup() {
        networkJsonObject = mock()
        responseRectifier = mock()
        responseAssembler = mock()
        materialDatabaseSource = mock()
        coroutineTestScope.setup()
        sut = SendDataAndRetrieveMaterialRemoteSource(
            coroutineScopes = coroutineTestScope.mock,
            networkJsonObject = networkJsonObject,
            responseRectifier = responseRectifier,
            responseAssembler = responseAssembler,
            materialDatabaseSource = materialDatabaseSource
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(
            networkJsonObject,
            responseRectifier,
            responseAssembler,
            materialDatabaseSource
        )
    }

    @Test
    fun `process returns assembled object when network responds`() = coroutineTestScope.run {
        val url = "http://server.com/api"
        val type = "type"
        val from = buildJsonObject { }
        val input = buildJsonObject { put("key", "value") }

        val networkResponse = buildJsonObject { put("network", "response") }
        val rectified = buildJsonObject { put("rectified", "ok") }
        val assembled = buildJsonObject { put("assembled", "final") }

        everySuspend { networkJsonObject.send(url, input) } returns networkResponse
        everySuspend { responseRectifier.process(networkResponse) } returns rectified
        everySuspend { materialDatabaseSource.getAllCommonRefOrNull(any(), any(), any()) } returns null
        everySuspend {
            responseAssembler.process(
                responseObject = rectified,
                findAllRefOrNullFetcher = any()
            )
        } calls { args ->
            val block = args.arg(1) as FindAllRefOrNullFetcherProtocol
            block.invoke(from, type)
            assembled
        }

        val result = sut.process(url, input)
        assertEquals(assembled, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.network.await<Any>(any())
            networkJsonObject.send(url, input)
            coroutineTestScope.mock.parser.await<Any>(any())
            responseRectifier.process(networkResponse)
            responseAssembler.process(rectified, any())
            coroutineTestScope.mock.database.await<Any>(any())
            materialDatabaseSource.getAllCommonRefOrNull(from, url, type)
        }
    }

    @Test
    fun `process returns null when network response is null`() = coroutineTestScope.run {
        val url = "http://server.com/api"
        val input = buildJsonObject { put("key", "value") }

        everySuspend { networkJsonObject.send(url, input) } returns null

        val result = sut.process(url, input)
        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.network.await<Any>(any())
            networkJsonObject.send(url, input)
        }
    }
}
