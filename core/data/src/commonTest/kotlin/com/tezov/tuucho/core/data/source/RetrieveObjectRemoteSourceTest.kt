package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RetrieveObjectRemoteSourceTest {

    private lateinit var currentScope: CoroutineScope
    private lateinit var coroutineScopes: CoroutineScopesProtocol
    private lateinit var materialNetworkSource: MaterialNetworkSourceProtocol
    private lateinit var sut: RetrieveObjectRemoteSource

    @BeforeTest
    fun setup() {
        currentScope = CoroutineScope(EmptyCoroutineContext)

        materialNetworkSource = mock()
        coroutineScopes = mock {
            val networkContext = mock<CoroutineContextProtocol> {
                everySuspend { await(block = any<suspend CoroutineScope.() -> JsonObject>()) } calls { (block: suspend CoroutineScope.() -> JsonObject) ->
                    block(currentScope)
                }
            }
            every { network } returns networkContext
        }

        sut = RetrieveObjectRemoteSource(
            coroutineScopes = coroutineScopes,
            materialNetworkSource = materialNetworkSource
        )
    }

    @Test
    fun `process returns object from network`() = runTest {
        val url = "http://server.com/api"
        val expected = buildJsonObject { put("result", "ok") }

        everySuspend { materialNetworkSource.retrieve(url) } returns expected

        val result = sut.process(url)

        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.network
            materialNetworkSource.retrieve(url)
        }
    }
}
