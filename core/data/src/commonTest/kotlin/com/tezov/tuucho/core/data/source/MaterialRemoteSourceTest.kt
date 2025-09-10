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

class MaterialRemoteSourceTest {

    private lateinit var currentScope: CoroutineScope
    private lateinit var coroutineScopes: CoroutineScopesProtocol
    private lateinit var materialNetworkSource: MaterialNetworkSourceProtocol
    private lateinit var materialRectifier: MaterialRectifierProtocol
    private lateinit var sut: MaterialRemoteSource

    @BeforeTest
    fun setup() {
        currentScope = CoroutineScope(EmptyCoroutineContext)

        materialNetworkSource = mock()
        materialRectifier = mock()

        coroutineScopes = mock {
            val networkContext = mock<CoroutineContextProtocol> {
                everySuspend { await(block = any<suspend CoroutineScope.() -> JsonObject>()) } calls {
                        (block: suspend CoroutineScope.() -> JsonObject) -> block(currentScope)
                }
            }
            every { network } returns networkContext

            val parserContext = mock<CoroutineContextProtocol> {
                everySuspend { await(block = any<suspend CoroutineScope.() -> JsonObject>()) } calls {
                        (block: suspend CoroutineScope.() -> JsonObject) -> block(currentScope)
                }
            }
            every { parser } returns parserContext
        }

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
