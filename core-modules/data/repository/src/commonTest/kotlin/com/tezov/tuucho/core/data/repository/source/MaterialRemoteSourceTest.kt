package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.mock.mockCoroutineScope
import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.repository.repository.source.MaterialRemoteSource
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
    private lateinit var networkJsonObject: NetworkJsonObject
    private lateinit var materialRectifier: MaterialRectifier
    private lateinit var sut: MaterialRemoteSource

    @BeforeTest
    fun setup() {
        coroutineScopes = mockCoroutineScope()
        networkJsonObject = mock()
        materialRectifier = mock()

        sut = MaterialRemoteSource(
            coroutineScopes = coroutineScopes,
            networkJsonObject = networkJsonObject,
            materialRectifier = materialRectifier
        )
    }

    @Test
    fun `process retrieves and rectifies response`() = runTest {
        val url = "http://server.com/api"
        val networkResponse = buildJsonObject { put("files", "data") }
        val expected = buildJsonObject { put("rectified", "ok") }

        everySuspend { networkJsonObject.resource(url) } returns networkResponse
        everySuspend { materialRectifier.process(networkResponse) } returns expected

        val result = sut.process(url)

        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.network
            networkJsonObject.resource(url)
            coroutineScopes.parser
            materialRectifier.process(networkResponse)
        }
    }
}
