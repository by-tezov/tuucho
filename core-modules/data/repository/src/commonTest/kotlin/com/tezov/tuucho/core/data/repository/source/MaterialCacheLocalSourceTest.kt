package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.entity.HookEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import com.tezov.tuucho.core.data.repository.mock.mockCoroutineScope
import com.tezov.tuucho.core.data.repository.parser._system.JsonObjectNode
import com.tezov.tuucho.core.data.repository.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.repository.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.repository.source._system.LifetimeResolver
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class MaterialCacheLocalSourceTest {
    private lateinit var coroutineScopes: CoroutineScopesProtocol
    private lateinit var materialDatabaseSource: MaterialDatabaseSource
    private lateinit var materialBreaker: MaterialBreaker
    private lateinit var materialAssembler: MaterialAssembler
    private lateinit var lifetimeResolver: LifetimeResolver

    private lateinit var sut: MaterialCacheLocalSource

    @BeforeTest
    fun setup() {
        coroutineScopes = mockCoroutineScope()
        materialDatabaseSource = mock()
        materialBreaker = mock()
        materialAssembler = mock()
        lifetimeResolver = mock()

        sut = MaterialCacheLocalSource(
            coroutineScopes = coroutineScopes,
            materialDatabaseSource = materialDatabaseSource,
            materialBreaker = materialBreaker,
            materialAssembler = materialAssembler,
            lifetimeResolver = lifetimeResolver
        )
    }

    @Test
    fun `isCacheValid returns false when no lifetime`() = runTest {
        val url = "url"
        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns null

        val result = sut.isCacheValid(url, "key")
        assertFalse(result)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.getLifetimeOrNull(url) }
    }

    @Test
    fun `isCacheValid returns false when keys doesn't match`() = runTest {
        val url = "url"
        val key1 = "key1"
        val key2 = "key2"
        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Unlimited(
            validityKey = key2
        )

        val result = sut.isCacheValid(url, key1)
        assertFalse(result)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.getLifetimeOrNull(url) }
    }

    @Test
    fun `isCacheValid returns true when unlimited lifetime and keys match`() = runTest {
        val url = "url"
        val key = "key"
        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Unlimited(
            validityKey = key
        )

        val result = sut.isCacheValid(url, key)
        assertTrue(result)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.getLifetimeOrNull(url) }
    }

    @Test
    fun `isCacheValid returns false when enrolled lifetime`() = runTest {
        val url = "url"
        val key = "key"
        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Enrolled(
            validityKey = key
        )

        val result = sut.isCacheValid(url, key)
        assertFalse(result)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.getLifetimeOrNull(url) }
    }

    @Test
    fun `isCacheValid returns true when transient lifetime expirationDateTime equal now`() = runTest {
        val url = "url"
        val key = "key"
        val fixed = Clock.System.now()
        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Transient(
            validityKey = key,
            expirationDateTime = fixed
        )

        val result = sut.isCacheValid(url, key) { fixed }
        assertTrue(result)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.getLifetimeOrNull(url) }
    }

    @Test
    fun `isCacheValid returns true when transient lifetime expirationDateTime above now`() = runTest {
        val url = "url"
        val key = "key"
        val fixed = Clock.System.now()
        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Transient(
            validityKey = key,
            expirationDateTime = fixed + 1.seconds
        )

        val result = sut.isCacheValid(url, key) { fixed }
        assertTrue(result)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.getLifetimeOrNull(url) }
    }

    @Test
    fun `isCacheValid returns false when transient lifetime expirationDateTime below now`() = runTest {
        val url = "url"
        val key = "key"
        val fixed = Clock.System.now()
        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Transient(
            validityKey = key,
            expirationDateTime = fixed - 1.seconds
        )

        val result = sut.isCacheValid(url, key) { fixed }
        assertFalse(result)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.getLifetimeOrNull(url) }
    }

    @Test
    fun `delete delegates to database`() = runTest {
        val url = "url"
        val table = Table.Common

        everySuspend { materialDatabaseSource.deleteAll(url, table) } returns Unit

        sut.delete(url, table)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.deleteAll(url, table) }
    }

    @Test
    fun `insert inserts root entity, hook, and children in common table`() = runTest {
        val url = "url"
        val visibility = Visibility.Local
        val weakLifetime = Lifetime.Unlimited("key")
        val lifetime = Lifetime.Unlimited("resolved")
        val material = buildJsonObject { put("key", "value") }
        val rootPrimaryKey = 3L

        val childNode = JsonObjectNode(
            buildJsonObject {
                put("id", buildJsonObject {
                    put("value", "valueChild")
                    put("source", "sourceChild")
                })
                put("type", "typeChild")
            }
        )
        val rootNode = JsonObjectNode(
            buildJsonObject {
                put("id", buildJsonObject {
                    put("value", "valueRoot")
                    put("source", "sourceRoot")
                })
                put("type", "typeRoot")
            }
        )
        val nodes = MaterialBreaker.Nodes(
            rootJsonObjectNode = rootNode,
            jsonElementNodes = listOf(childNode)
        )

        everySuspend { materialBreaker.process(material) } returns nodes
        everySuspend { materialDatabaseSource.insert(any(), Table.Common) } returns rootPrimaryKey
        everySuspend { materialDatabaseSource.insert(any()) } returns Unit
        every { lifetimeResolver.invoke(any(), weakLifetime) } returns lifetime

        sut.insert(material, url, visibility, weakLifetime)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.parser
            materialBreaker.process(material)
            coroutineScopes.database
            materialDatabaseSource.insert(any(), Table.Common)
            materialDatabaseSource.insert(any())
            materialDatabaseSource.insert(any(), Table.Common)
        }
        verify(VerifyMode.exactly(1)) { coroutineScopes.parser }
        verifySuspend(VerifyMode.exactly(1)) { materialBreaker.process(material) }
        verify(VerifyMode.exactly(1)) { coroutineScopes.database }
        verifySuspend(VerifyMode.exactly(1)) {
            materialDatabaseSource.insert(
                matching { entity: JsonObjectEntity ->
                    entity.type == "typeRoot" &&
                        entity.url == url &&
                        entity.id == "valueRoot" &&
                        entity.idFrom == "sourceRoot" &&
                        entity.jsonObject == rootNode.content
                },
                Table.Common
            )
        }
        verifySuspend(VerifyMode.exactly(1)) {
            materialDatabaseSource.insert(
                matching { hook: HookEntity ->
                    hook.url == url &&
                        hook.visibility == visibility &&
                        hook.rootPrimaryKey == rootPrimaryKey &&
                        hook.lifetime == lifetime
                }
            )
        }
        verifySuspend(VerifyMode.exactly(1)) {
            materialDatabaseSource.insert(
                matching { entity: JsonObjectEntity ->
                    entity.type == "typeChild" &&
                        entity.url == url &&
                        entity.id == "valueChild" &&
                        entity.idFrom == "sourceChild" &&
                        entity.jsonObject == childNode.content
                },
                Table.Common
            )
        }
    }

    @Test
    fun `insert inserts hook, and node with children in contextual table`() = runTest {
        val url = "url"
        val visibility = Visibility.Contextual(urlOrigin = "urlOrigin")
        val weakLifetime = Lifetime.Unlimited("key")
        val material = buildJsonObject { put("key", "value") }
        val rootPrimaryKey = 3L

        val childNode = JsonObjectNode(
            buildJsonObject {
                put("id", buildJsonObject {
                    put("value", "valueChild")
                    put("source", "sourceChild")
                })
                put("type", "typeChild")
            }
        )
        val node = JsonObjectNode(
            buildJsonObject {
                put("id", buildJsonObject {
                    put("value", "value")
                    put("source", "source")
                })
                put("type", "typeRoot")
            }
        ).apply { children = listOf(childNode, childNode, childNode) }
        val nodes = MaterialBreaker.Nodes(
            rootJsonObjectNode = null,
            jsonElementNodes = listOf(node)
        )

        everySuspend { materialBreaker.process(material) } returns nodes
        everySuspend {
            materialDatabaseSource.insert(
                any(),
                Table.Contextual
            )
        } returns rootPrimaryKey
        everySuspend { materialDatabaseSource.insert(any()) } returns Unit
        every { lifetimeResolver.invoke(any(), weakLifetime) } returns weakLifetime

        sut.insert(material, url, visibility, weakLifetime)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.parser
            materialBreaker.process(material)
            coroutineScopes.database
            materialDatabaseSource.insert(any())
            materialDatabaseSource.insert(any(), Table.Contextual)
            materialDatabaseSource.insert(any(), Table.Contextual)
            materialDatabaseSource.insert(any(), Table.Contextual)
            materialDatabaseSource.insert(any(), Table.Contextual)
        }
    }

    @Test
    fun `enroll inserts enrolled hook`() = runTest {
        val url = "url"
        val key = "key"
        val visibility = Visibility.Local

        everySuspend { materialDatabaseSource.insert(any()) } returns Unit

        sut.enroll(url, key, visibility)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.database
            materialDatabaseSource.insert(any())
        }
        verifySuspend(VerifyMode.exactly(1)) {
            materialDatabaseSource.insert(matching(
                toString = { "HookEntity with expected values" }
            ) { hook: HookEntity ->
                hook.url == url &&
                    hook.visibility == visibility &&
                    (hook.lifetime as? Lifetime.Enrolled)?.validityKey == key
            })
        }
    }

    @Test
    fun `getLifetime returns value from db`() = runTest {
        val url = "url"
        val lifetime = Lifetime.Unlimited("k")

        everySuspend { materialDatabaseSource.getHookEntityOrNull(url) } returns HookEntity(
            null,
            url,
            null,
            Visibility.Global,
            lifetime
        )

        val result = sut.getLifetime(url)
        assertEquals(lifetime, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.database
            materialDatabaseSource.getHookEntityOrNull(url)
        }
    }

    @Test
    fun `getLifetime returns null`() = runTest {
        val url = "url"

        everySuspend { materialDatabaseSource.getHookEntityOrNull(url) } returns null

        val result = sut.getLifetime(url)
        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.database
            materialDatabaseSource.getHookEntityOrNull(url)
        }
    }

    @Test
    fun `assemble returns null when no root entity`() = runTest {
        val url = "url"
        everySuspend { materialDatabaseSource.getRootJsonObjectEntityOrNull(url) } returns null

        val result = sut.assemble(url)

        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.database
            materialDatabaseSource.getRootJsonObjectEntityOrNull(url)
        }
    }

    @Test
    fun `assemble passes root entity to assembler`() = runTest {
        val url = "url"
        val entity = JsonObjectEntity(
            type = "type",
            url = url,
            id = "id",
            idFrom = "idFrom",
            jsonObject = buildJsonObject { put("x", "y") }
        )
        val expected = buildJsonObject { put("assembled", "ok") }

        everySuspend { materialDatabaseSource.getRootJsonObjectEntityOrNull(url) } returns entity
        everySuspend { materialAssembler.process(entity.jsonObject, any()) } returns expected

        val result = sut.assemble(url)
        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineScopes.database
            materialDatabaseSource.getRootJsonObjectEntityOrNull(url)
            coroutineScopes.parser
            materialAssembler.process(entity.jsonObject, any())
        }
    }
}
