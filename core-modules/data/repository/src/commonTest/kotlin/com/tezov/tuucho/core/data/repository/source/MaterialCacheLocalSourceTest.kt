package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.entity.HookEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import com.tezov.tuucho.core.data.repository.mock.coroutineTestScope
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.repository.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.repository.source._system.LifetimeResolver
import com.tezov.tuucho.core.domain.tool.json.string
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class MaterialCacheLocalSourceTest {
    private val coroutineTestScope = coroutineTestScope()
    private lateinit var materialDatabaseSource: MaterialDatabaseSource
    private lateinit var materialBreaker: MaterialBreaker
    private lateinit var materialAssembler: MaterialAssembler
    private lateinit var lifetimeResolver: LifetimeResolver

    private lateinit var sut: MaterialCacheLocalSource

    @BeforeTest
    fun setup() {
        materialDatabaseSource = mock()
        materialBreaker = mock()
        materialAssembler = mock()
        lifetimeResolver = mock()

        sut = MaterialCacheLocalSource(
            coroutineScopes = coroutineTestScope.createMock(),
            materialDatabaseSource = materialDatabaseSource,
            materialBreaker = materialBreaker,
            materialAssembler = materialAssembler,
            lifetimeResolver = lifetimeResolver
        )
    }

    @Test
    fun `isCacheValid returns false when no lifetime`() = coroutineTestScope.run {
        val url = "url"
        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns null

        val result = sut.isCacheValid(url, "key")
        assertFalse(result)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.getLifetimeOrNull(url) }
    }

    @Test
    fun `isCacheValid returns false when keys doesn't match`() = coroutineTestScope.run {
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
    fun `isCacheValid returns true when unlimited lifetime and keys match`() = coroutineTestScope.run {
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
    fun `isCacheValid returns false when enrolled lifetime`() = coroutineTestScope.run {
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
    fun `isCacheValid returns true when transient lifetime expirationDateTime equal now`() = coroutineTestScope.run {
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
    fun `isCacheValid returns true when transient lifetime expirationDateTime above now`() = coroutineTestScope.run {
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
    fun `isCacheValid returns false when transient lifetime expirationDateTime below now`() = coroutineTestScope.run {
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
    fun `delete delegates to database`() = coroutineTestScope.run {
        val url = "url"
        val table = Table.Common

        everySuspend { materialDatabaseSource.deleteAll(url, table) } returns Unit

        sut.delete(url, table)

        verifySuspend(VerifyMode.exactly(1)) { materialDatabaseSource.deleteAll(url, table) }
    }

    @Test
    fun `insert inserts root entity, hook, and children in common table`() = coroutineTestScope.run {
        val url = "url"
        val visibility = Visibility.Local
        val weakLifetime = Lifetime.Unlimited("key")
        val lifetime = Lifetime.Unlimited("resolved")
        val material = buildJsonObject { put("key", JsonPrimitive("value")) }
        val rootPrimaryKey = 3L

        val childNode = buildJsonObject {
            put("id", buildJsonObject {
                put("value", JsonPrimitive("valueChild"))
                put("source", JsonPrimitive("sourceChild"))
            })
            put("type", JsonPrimitive("typeChild"))
        }
        val rootNode = buildJsonObject {
            put("id", buildJsonObject {
                put("value", JsonPrimitive("valueRoot"))
                put("source", JsonPrimitive("sourceRoot"))
            })
            put("type", JsonPrimitive("typeRoot"))
        }
        val nodes = MaterialBreaker.Nodes(
            rootJsonObject = rootNode,
            jsonObjects = listOf(childNode)
        )

        everySuspend { materialBreaker.process(material) } returns nodes
        everySuspend { materialDatabaseSource.insert(any(), Table.Common) } returns rootPrimaryKey
        everySuspend { materialDatabaseSource.insert(any()) } returns Unit
        every { lifetimeResolver.invoke(any(), weakLifetime) } returns lifetime

        sut.insert(material, url, visibility, weakLifetime)

        verify(VerifyMode.exactly(1)) { coroutineTestScope.mock.parser }
        verifySuspend(VerifyMode.exactly(1)) { materialBreaker.process(material) }
        verify(VerifyMode.exactly(1)) { coroutineTestScope.mock.database }
        verifySuspend(VerifyMode.exactly(1)) {
            materialDatabaseSource.insert(
                matches { entity: JsonObjectEntity ->
                    entity.type == "typeRoot" &&
                        entity.url == url &&
                        entity.id == "valueRoot" &&
                        entity.idFrom == "sourceRoot" &&
                        entity.jsonObject == rootNode
                },
                Table.Common
            )
        }
        verifySuspend(VerifyMode.exactly(1)) {
            materialDatabaseSource.insert(
                matches { hook: HookEntity ->
                    hook.url == url &&
                        hook.visibility == visibility &&
                        hook.rootPrimaryKey == rootPrimaryKey &&
                        hook.lifetime == lifetime
                }
            )
        }
        verifySuspend(VerifyMode.exactly(1)) {
            materialDatabaseSource.insert(
                matches { entity: JsonObjectEntity ->
                    entity.type == "typeChild" &&
                        entity.url == url &&
                        entity.id == "valueChild" &&
                        entity.idFrom == "sourceChild" &&
                        entity.jsonObject == childNode
                },
                Table.Common
            )
        }
    }

    @Test
    fun `insert hook, and node with children in contextual table`() = coroutineTestScope.run {
        val url = "url"
        val visibility = Visibility.Contextual(urlOrigin = "urlOrigin")
        val lifetime = Lifetime.Unlimited("key")
        val material = buildJsonObject { put("key", JsonPrimitive("value")) }
        val rootPrimaryKey = 3L

        val node1 = buildJsonObject {
            put("id", buildJsonObject {
                put("value", JsonPrimitive("value1"))
                put("source", JsonPrimitive("source1"))
            })
            put("type", JsonPrimitive("node1"))
        }
        val node2 = buildJsonObject {
            put("id", buildJsonObject {
                put("value", JsonPrimitive("value2"))
                put("source", JsonPrimitive("source2"))
            })
            put("type", JsonPrimitive("node2"))
        }
        val nodes = MaterialBreaker.Nodes(
            rootJsonObject = null,
            jsonObjects = listOf(node1, node2)
        )

        everySuspend { materialBreaker.process(material) } returns nodes
        everySuspend {
            materialDatabaseSource.insert(any(), Table.Contextual)
        } returns rootPrimaryKey
        everySuspend { materialDatabaseSource.insert(any()) } returns Unit
        every { lifetimeResolver.invoke(any(), lifetime) } returns lifetime

        sut.insert(material, url, visibility, lifetime)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.parser
            materialBreaker.process(material)
            coroutineTestScope.mock.database
        }
        verifySuspend(exactly(1)) {
            materialDatabaseSource.insert(matches { hook: HookEntity ->
                hook.url == url &&
                    hook.visibility == visibility &&
                    hook.rootPrimaryKey == null &&
                    hook.lifetime == lifetime
            })
        }
        verifySuspend(exactly(1)) {
            materialDatabaseSource.insert(matches { entity: JsonObjectEntity ->
                entity.type == node1["type"].string &&
                    entity.url == url &&
                    entity.id == node1["id"]?.jsonObject["value"].string &&
                    entity.idFrom == node1["id"]?.jsonObject["source"].string &&
                    entity.jsonObject == node1
            }, Table.Contextual)
        }
        verifySuspend(exactly(1)) {
            materialDatabaseSource.insert(matches { entity: JsonObjectEntity ->
                entity.type == node2["type"].string &&
                    entity.url == url &&
                    entity.id == node2["id"]?.jsonObject["value"].string &&
                    entity.idFrom == node2["id"]?.jsonObject["source"].string &&
                    entity.jsonObject == node2
            }, Table.Contextual)
        }
    }

    @Test
    fun `enroll inserts enrolled hook`() = coroutineTestScope.run {
        val url = "url"
        val key = "key"
        val visibility = Visibility.Local

        everySuspend { materialDatabaseSource.insert(any()) } returns Unit

        sut.enroll(url, key, visibility)

        verify(VerifyMode.exactly(1)) {
            coroutineTestScope.mock.database
        }
        verifySuspend(VerifyMode.exactly(1)) {
            materialDatabaseSource.insert(matches(
                toString = { "HookEntity with expected values" }
            ) { hook: HookEntity ->
                hook.url == url &&
                    hook.visibility == visibility &&
                    (hook.lifetime as? Lifetime.Enrolled)?.validityKey == key
            })
        }
    }

    @Test
    fun `getLifetime returns value from db`() = coroutineTestScope.run {
        val url = "url"
        val lifetime = Lifetime.Unlimited("k")

        everySuspend { materialDatabaseSource.getHookEntityOrNull(url) } returns HookEntity(
            primaryKey = null,
            url = url,
            rootPrimaryKey = null,
            visibility = Visibility.Global,
            lifetime = lifetime
        )

        val result = sut.getLifetime(url)
        assertEquals(lifetime, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.database
            materialDatabaseSource.getHookEntityOrNull(url)
        }
    }

    @Test
    fun `getLifetime returns null`() = coroutineTestScope.run {
        val url = "url"

        everySuspend { materialDatabaseSource.getHookEntityOrNull(url) } returns null

        val result = sut.getLifetime(url)
        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.database
            materialDatabaseSource.getHookEntityOrNull(url)
        }
    }

    @Test
    fun `assemble returns null when no root entity`() = coroutineTestScope.run {
        val url = "url"
        everySuspend { materialDatabaseSource.getRootJsonObjectEntityOrNull(url) } returns null

        val result = sut.assemble(url)

        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.database
            materialDatabaseSource.getRootJsonObjectEntityOrNull(url)
        }
    }

    @Test
    fun `assemble passes root entity to assembler`() = coroutineTestScope.run {
        val url = "url"
        val entity = JsonObjectEntity(
            type = "type",
            url = url,
            id = "id",
            idFrom = "idFrom",
            jsonObject = buildJsonObject { put("x", JsonPrimitive("y")) }
        )
        val expected = buildJsonObject { put("assembled", JsonPrimitive("ok")) }

        everySuspend { materialDatabaseSource.getRootJsonObjectEntityOrNull(url) } returns entity
        everySuspend { materialAssembler.process(entity.jsonObject, any()) } returns expected

        val result = sut.assemble(url)
        assertEquals(expected, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.database
            materialDatabaseSource.getRootJsonObjectEntityOrNull(url)
            coroutineTestScope.mock.parser
            materialAssembler.process(entity.jsonObject, any())
        }
    }
}
