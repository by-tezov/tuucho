package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.mock.coroutineTestScope
import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.repository.source.SendDataAndRetrieveMaterialRemoteSource
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SendDataAndRetrieveMaterialRemoteSourceTest {
    private val coroutineTestScope = coroutineTestScope()
    private lateinit var networkJsonObject: NetworkJsonObject

    private lateinit var sut: SendDataAndRetrieveMaterialRemoteSource

    @BeforeTest
    fun setup() {
        networkJsonObject = mock()

        sut = SendDataAndRetrieveMaterialRemoteSource(
            coroutineScopes = coroutineTestScope.createMock(),
            networkJsonObject = networkJsonObject,
        )
    }

    @Test
    fun `process returns rectified object when network responds`() = coroutineTestScope.run {
        val url = "http://server.com/api"
        val input = buildJsonObject { put("key", "value") }
        val expected = buildJsonObject { put("network", "response") }

        everySuspend { networkJsonObject.send(url, input) } returns expected

        val result = sut.process(url, input)
        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.network
            networkJsonObject.send(url, input)
        }
    }

    @Test
    fun `process returns null when network response is null`() = coroutineTestScope.run {
        val url = "http://server.com/api"
        val input = buildJsonObject { put("key", "value") }

        everySuspend { networkJsonObject.send(url, input) } returns null

        val result = sut.process(url, input)
        assertNull(result)
    }
}
