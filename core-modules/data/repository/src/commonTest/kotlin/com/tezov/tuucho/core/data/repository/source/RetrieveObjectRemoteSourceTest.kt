package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.mock.mockCoroutineScope
import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.repository.source.RetrieveObjectRemoteSource
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

class RetrieveObjectRemoteSourceTest {

    private lateinit var coroutineScopes: CoroutineScopesProtocol
    private lateinit var networkJsonObject: NetworkJsonObject
    private lateinit var sut: RetrieveObjectRemoteSource

    @BeforeTest
    fun setup() {
        coroutineScopes = mockCoroutineScope()
        networkJsonObject = mock()

        sut = RetrieveObjectRemoteSource(
            coroutineScopes = coroutineScopes,
            networkJsonObject = networkJsonObject
        )
    }

    @Test
    fun `process returns object from network`() = runTest {
        val url = "http://server.com/api"
        val expected = buildJsonObject { put("result", "ok") }

        everySuspend { networkJsonObject.resource(url) } returns expected

        val result = sut.process(url)

        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.network
            networkJsonObject.resource(url)
        }
    }
}
