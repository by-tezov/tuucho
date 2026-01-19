package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.mock.CoroutineTestScope
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.data.repository.repository.source.MaterialRemoteSource
import com.tezov.tuucho.core.data.repository.repository.source.RemoteSource
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

class MaterialRemoteSourceTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var remoteSource: RemoteSource
    private lateinit var materialRectifier: MaterialRectifier
    private lateinit var sut: MaterialRemoteSource

    @BeforeTest
    fun setup() {
        remoteSource = mock()
        materialRectifier = mock()
        coroutineTestScope.setup()
        sut = MaterialRemoteSource(
            coroutineScopes = coroutineTestScope.mock,
            remoteSource = remoteSource,
            materialRectifier = materialRectifier
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(
            remoteSource,
        )
    }

    @Test
    fun `process retrieves and rectifies response`() = coroutineTestScope.run {
        val url = "http://server.com/api"
        val networkResponse = buildJsonObject { put("files", "data") }
        val expected = buildJsonObject { put("rectified", "ok") }

        everySuspend { remoteSource.resource(url) } returns networkResponse
        everySuspend { materialRectifier.process(networkResponse) } returns expected

        val result = sut.process(url)

        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            remoteSource.resource(url)
            coroutineTestScope.mock.parser.await<Any>(any())
            materialRectifier.process(networkResponse)
        }
    }
}
