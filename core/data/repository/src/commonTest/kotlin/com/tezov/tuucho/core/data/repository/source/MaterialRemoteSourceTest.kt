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

class MaterialRemoteSourceTest {

    private lateinit var coroutineScopes: CoroutineScopesProtocol
    private lateinit var materialNetworkSource: MaterialNetworkSource
    private lateinit var materialRectifier: MaterialRectifier
    private lateinit var sut: MaterialRemoteSource

    @BeforeTest
    fun setup() {
        coroutineScopes = mockCoroutineScope()
        materialNetworkSource = mock()
        materialRectifier = mock()

        sut = MaterialRemoteSource(
            coroutineScopes = coroutineScopes,
            materialNetworkSource = materialNetworkSource,
            materialRectifier = materialRectifier
        )
    }

    @Test
    fun `process retrieves and rectifies response`() = runTest {
        val url = "http://server.com/api"
        val networkResponse = buildJsonObject { put("raw", "data") }
        val expected = buildJsonObject { put("rectified", "ok") }

        everySuspend { materialNetworkSource.retrieve(url) } returns networkResponse
        everySuspend { materialRectifier.process(networkResponse) } returns expected

        val result = sut.process(url)

        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.network
            materialNetworkSource.retrieve(url)
            coroutineScopes.parser
            materialRectifier.process(networkResponse)
        }
    }
}
