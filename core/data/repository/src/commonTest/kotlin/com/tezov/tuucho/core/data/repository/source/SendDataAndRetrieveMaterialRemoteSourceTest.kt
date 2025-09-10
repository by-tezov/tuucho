package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.mock.mockCoroutineScope
import com.tezov.tuucho.core.data.repository.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.repository.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SendDataAndRetrieveMaterialRemoteSourceTest {

    private lateinit var coroutineScopes: CoroutineScopesProtocol
    private lateinit var materialNetworkSource: MaterialNetworkSource
    private lateinit var materialRectifier: MaterialRectifier

    private lateinit var sut: SendDataAndRetrieveMaterialRemoteSource

    @BeforeTest
    fun setup() {
        coroutineScopes = mockCoroutineScope()
        materialNetworkSource = mock()
        materialRectifier = mock()

        sut = SendDataAndRetrieveMaterialRemoteSource(
            coroutineScopes = coroutineScopes,
            materialNetworkSource = materialNetworkSource,
            materialRectifier = materialRectifier
        )
    }

    @Test
    fun `process returns rectified object when network responds`() = runTest {
        val url = "http://server.com/api"
        val input = buildJsonObject { put("key", "value") }
        val networkResponse = buildJsonObject { put("network", "response") }
        val expected = buildJsonObject { put("rectified", "ok") }

        everySuspend { materialNetworkSource.send(url, input) } returns networkResponse
        everySuspend { materialRectifier.process(networkResponse) } returns expected

        val result = sut.process(url, input)
        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.network
            materialNetworkSource.send(url, input)
            coroutineScopes.parser
            materialRectifier.process(networkResponse)
        }
    }

    @Test
    fun `process returns null when network response is null`() = runTest {
        val url = "http://server.com/api"
        val input = buildJsonObject { put("key", "value") }

        everySuspend { materialNetworkSource.send(url, input) } returns null

        val result = sut.process(url, input)
        assertNull(result)
    }
}
