package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.mock.coroutineTestScope
import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.repository.source.RetrieveObjectRemoteSource
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

class RetrieveObjectRemoteSourceTest {
    private val coroutineTestScope = coroutineTestScope()
    private lateinit var networkJsonObject: NetworkJsonObject
    private lateinit var sut: RetrieveObjectRemoteSource

    @BeforeTest
    fun setup() {
        networkJsonObject = mock()

        sut = RetrieveObjectRemoteSource(
            coroutineScopes = coroutineTestScope.createMock(),
            networkJsonObject = networkJsonObject
        )
    }

    @Test
    fun `process returns object from network`() = coroutineTestScope.run {
        val url = "http://server.com/api"
        val expected = buildJsonObject { put("result", "ok") }

        everySuspend { networkJsonObject.resource(url) } returns expected

        val result = sut.process(url)

        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.network
            networkJsonObject.resource(url)
        }
    }
}
