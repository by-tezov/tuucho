package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.network.MaterialNetworkSourceProtocol
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifierProtocol
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
import kotlin.test.assertNull

class SendDataAndRetrieveMaterialRemoteSourceTest {

    private lateinit var currentScope: CoroutineScope
    private lateinit var coroutineScopes: CoroutineScopesProtocol
    private lateinit var materialNetworkSource: MaterialNetworkSourceProtocol
    private lateinit var materialRectifier: MaterialRectifierProtocol

    private lateinit var sut: SendDataAndRetrieveMaterialRemoteSource

    @BeforeTest
    fun setup() {
        currentScope = CoroutineScope(
            context = EmptyCoroutineContext,
        )

        materialNetworkSource = mock()
        materialRectifier = mock()

        coroutineScopes = mock {
            val networkContext = mock<CoroutineContextProtocol> {
                everySuspend { await(block = any<suspend CoroutineScope.() -> JsonObject?>()) } calls {
                    (block: suspend CoroutineScope.() -> JsonObject?) -> block(currentScope)
                }
            }
            every { network } returns networkContext

            val parserContext = mock<CoroutineContextProtocol> {
                everySuspend { await(block = any<suspend CoroutineScope.() -> JsonObject?>()) } calls {
                    (block: suspend CoroutineScope.() -> JsonObject?) -> block(currentScope)
                }
            }
            every { parser } returns parserContext
        }

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
