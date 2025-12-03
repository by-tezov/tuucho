package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.mock.CoroutineTestScope
import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.repository.source.RetrieveObjectRemoteSource
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

class RetrieveObjectRemoteSourceTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var networkJsonObject: NetworkJsonObject
    private lateinit var sut: RetrieveObjectRemoteSource

    @BeforeTest
    fun setup() {
        networkJsonObject = mock()
        coroutineTestScope.setup()
        sut = RetrieveObjectRemoteSource(
            coroutineScopes = coroutineTestScope.mock,
            networkJsonObject = networkJsonObject
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(
            networkJsonObject,
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
            coroutineTestScope.mock.network.await<Any>(any())
            networkJsonObject.resource(url)
        }
    }
}
