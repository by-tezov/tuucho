package com.tezov.tuucho.core.data.repository.source

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.entity.HookEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import com.tezov.tuucho.core.data.repository.mock.CoroutineTestScope
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.FindAllRefOrNullFetcherProtocol
import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.repository.repository.source.MaterialCacheLocalSource
import com.tezov.tuucho.core.data.repository.repository.source._system.LifetimeResolver
import com.tezov.tuucho.core.domain.tool.json.string
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.MokkeryMatcherScope
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exhaustiveOrder
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class MaterialCacheLocalSourceTest {
    private val coroutineTestScope = CoroutineTestScope()
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
        coroutineTestScope.setup()
        sut = MaterialCacheLocalSource(
            coroutineScopes = coroutineTestScope.mock,
            materialDatabaseSource = materialDatabaseSource,
            materialBreaker = materialBreaker,
            materialAssembler = materialAssembler,
            lifetimeResolver = lifetimeResolver
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(
            materialDatabaseSource,
            materialBreaker,
            materialAssembler,
            lifetimeResolver
        )
    }

    @Test
    fun `isCacheValid returns false when no lifetime`() = coroutineTestScope.run {
        val url = "url"

        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns null

        val result = sut.isCacheValid(url, "key")
        assertFalse(result)

        verifySuspend(exhaustiveOrder) {
            materialDatabaseSource.getLifetimeOrNull(url)
        }
    }

    @Test
    fun `isCacheValid returns false when keys doesn't match`() = coroutineTestScope.run {
        val url = "url"
        val key1 = "key1"
        val key2 = "key2"

        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Unlimited(key2)

        val result = sut.isCacheValid(url, key1)
        assertFalse(result)

        verifySuspend(exhaustiveOrder) {
            materialDatabaseSource.getLifetimeOrNull(url)
        }
    }

    @Test
    fun `isCacheValid returns true when unlimited and keys match`() = coroutineTestScope.run {
        val url = "url"
        val key = "key"

        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Unlimited(key)

        val result = sut.isCacheValid(url, key)
        assertTrue(result)

        verifySuspend(exhaustiveOrder) {
            materialDatabaseSource.getLifetimeOrNull(url)
        }
    }

    @Test
    fun `isCacheValid returns false when enrolled`() = coroutineTestScope.run {
        val url = "url"
        val key = "key"

        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Enrolled(key)

        val result = sut.isCacheValid(url, key)
        assertFalse(result)

        verifySuspend(exhaustiveOrder) {
            materialDatabaseSource.getLifetimeOrNull(url)
        }
    }

    @Test
    fun `isCacheValid returns true when transient and expiration == now`() = coroutineTestScope.run {
        val url = "url"
        val key = "key"
        val now = Clock.System.now()

        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Transient(key, now)

        val result = sut.isCacheValid(url, key) { now }
        assertTrue(result)

        verifySuspend(exhaustiveOrder) {
            materialDatabaseSource.getLifetimeOrNull(url)
        }
    }

    @Test
    fun `isCacheValid returns true when transient and expiration future`() = coroutineTestScope.run {
        val url = "url"
        val key = "key"
        val now = Clock.System.now()

        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Transient(key, now + 1.seconds)

        val result = sut.isCacheValid(url, key) { now }
        assertTrue(result)

        verifySuspend(exhaustiveOrder) {
            materialDatabaseSource.getLifetimeOrNull(url)
        }
    }

    @Test
    fun `isCacheValid returns false when transient and expiration past`() = coroutineTestScope.run {
        val url = "url"
        val key = "key"
        val now = Clock.System.now()

        everySuspend { materialDatabaseSource.getLifetimeOrNull(url) } returns Lifetime.Transient(key, now - 1.seconds)

        val result = sut.isCacheValid(url, key) { now }
        assertFalse(result)

        verifySuspend(exhaustiveOrder) {
            materialDatabaseSource.getLifetimeOrNull(url)
        }
    }

    @Test
    fun `delete delegates to database`() = coroutineTestScope.run {
        val url = "url"
        val table = Table.Common

        everySuspend { materialDatabaseSource.deleteAll(url, table) } returns Unit

        sut.delete(url, table)

        verifySuspend(exhaustiveOrder) {
            materialDatabaseSource.deleteAll(url, table)
        }
    }

    private fun MokkeryMatcherScope.matches(
        type: String,
        url: String,
        id: String,
        idFrom: String,
        jsonObject: JsonObject,
    ) = matches<JsonObjectEntity> { entity ->
        entity.type == type &&
            entity.url == url &&
            entity.id == id &&
            entity.idFrom == idFrom &&
            entity.jsonObject == jsonObject
    }

    private fun MokkeryMatcherScope.matches(
        url: String,
        rootPrimaryKey: Long?,
        visibility: Visibility,
        lifetime: Lifetime,
    ) = matches<HookEntity> { entity ->
        entity.url == url &&
            entity.visibility == visibility &&
            entity.rootPrimaryKey == rootPrimaryKey &&
            entity.lifetime == lifetime
    }

    @Test
    fun `insert inserts root, hook, and children in common table`() = coroutineTestScope.run {
        val url = "url"
        val visibility = Visibility.Local
        val weak = Lifetime.Unlimited("keyWeak")
        val resolved = Lifetime.Unlimited("resolved")

        val jsonMaterial = buildJsonObject { put("key", JsonPrimitive("v")) }
        val rootPrimaryKey = 5L

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

        everySuspend { materialBreaker.process(jsonMaterial) } returns nodes
        everySuspend { materialDatabaseSource.insert(any(), Table.Common) } returns rootPrimaryKey
        everySuspend { materialDatabaseSource.insert(any()) } returns Unit
        every { lifetimeResolver.invoke(any(), weak) } returns resolved

        sut.insert(jsonMaterial, url, visibility, weak)

        verifySuspend(exhaustiveOrder) {
            coroutineTestScope.mock.parser.await<Any>(any())
            materialBreaker.process(jsonMaterial)

            coroutineTestScope.mock.database.await<Any>(any())
            materialDatabaseSource.insert(
                matches(
                    type = "typeRoot",
                    url = url,
                    id = "valueRoot",
                    idFrom = "sourceRoot",
                    jsonObject = rootNode,
                ),
                Table.Common
            )

            lifetimeResolver.invoke(null, weak)

            materialDatabaseSource.insert(
                matches(
                    url = url,
                    rootPrimaryKey = rootPrimaryKey,
                    visibility = visibility,
                    lifetime = resolved,
                )
            )

            materialDatabaseSource.insert(
                matches(
                    type = "typeChild",
                    url = url,
                    id = "valueChild",
                    idFrom = "sourceChild",
                    jsonObject = childNode,
                ),
                Table.Common
            )
        }
    }

    @Test
    fun `insert contextual`() = coroutineTestScope.run {
        val url = "url"
        val visibility = Visibility.Contextual("urlOrigin")
        val lifetime = Lifetime.Unlimited("key")

        val material = buildJsonObject { put("key", JsonPrimitive("value")) }
        val rootPrimaryKey = 9L

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
        everySuspend { materialDatabaseSource.insert(any(), Table.Contextual) } returns rootPrimaryKey
        everySuspend { materialDatabaseSource.insert(any()) } returns Unit
        every { lifetimeResolver.invoke(any(), lifetime) } returns lifetime

        sut.insert(material, url, visibility, lifetime)

        verifySuspend(exhaustiveOrder) {
            coroutineTestScope.mock.parser.await<Any>(any())
            materialBreaker.process(material)

            coroutineTestScope.mock.database.await<Any>(any())
            lifetimeResolver.invoke(null, lifetime)

            materialDatabaseSource.insert(matches { h ->
                h.url == url &&
                    h.visibility == visibility &&
                    h.rootPrimaryKey == null &&
                    h.lifetime == lifetime
            })

            materialDatabaseSource.insert(
                matches(
                    type = node1["type"].string,
                    url = url,
                    id = node1["id"]?.jsonObject["value"].string,
                    idFrom = node1["id"]?.jsonObject["source"].string,
                    jsonObject = node1,
                ),
                Table.Contextual
            )

            materialDatabaseSource.insert(
                matches(
                    type = node2["type"].string,
                    url = url,
                    id = node2["id"]?.jsonObject["value"].string,
                    idFrom = node2["id"]?.jsonObject["source"].string,
                    jsonObject = node2,
                ),
                Table.Contextual
            )
        }
    }

    @Test
    fun `enroll inserts enrolled hook`() = coroutineTestScope.run {
        val url = "url"
        val key = "key"
        val visibility = Visibility.Local

        everySuspend { materialDatabaseSource.insert(any()) } returns Unit

        sut.enroll(url, key, visibility)

        verifySuspend(exhaustiveOrder) {
            coroutineTestScope.mock.database.await<Any>(any())
            materialDatabaseSource.insert(matches { entity ->
                entity.url == url &&
                    entity.visibility == visibility &&
                    (entity.lifetime as? Lifetime.Enrolled)?.validityKey == key
            })
        }
    }

    @Test
    fun `getLifetime returns lifetime`() = coroutineTestScope.run {
        val url = "url"
        val lifetime = Lifetime.Unlimited("key")

        everySuspend { materialDatabaseSource.getHookEntityOrNull(url) } returns HookEntity(
            primaryKey = null,
            url = url,
            rootPrimaryKey = null,
            visibility = Visibility.Global,
            lifetime = lifetime
        )

        val res = sut.getLifetime(url)
        assertEquals(lifetime, res)

        verifySuspend(exhaustiveOrder) {
            coroutineTestScope.mock.database.await<Any>(any())
            materialDatabaseSource.getHookEntityOrNull(url)
        }
    }

    @Test
    fun `getLifetime returns null`() = coroutineTestScope.run {
        val url = "url"

        everySuspend { materialDatabaseSource.getHookEntityOrNull(url) } returns null

        val res = sut.getLifetime(url)
        assertNull(res)

        verifySuspend(exhaustiveOrder) {
            coroutineTestScope.mock.database.await<Any>(any())
            materialDatabaseSource.getHookEntityOrNull(url)
        }
    }

    @Test
    fun `assemble returns null when root missing`() = coroutineTestScope.run {
        val url = "url"

        everySuspend { materialDatabaseSource.getRootJsonObjectEntityOrNull(url) } returns null

        val res = sut.assemble(url)
        assertNull(res)

        verifySuspend(exhaustiveOrder) {
            coroutineTestScope.mock.database.await<Any>(any())
            materialDatabaseSource.getRootJsonObjectEntityOrNull(url)
        }
    }

    @Test
    fun `assemble passes root to assembler`() = coroutineTestScope.run {
        val url = "url"
        val type = "type"
        val from = buildJsonObject { }

        val entity = JsonObjectEntity(
            type = "type",
            url = url,
            id = "id",
            idFrom = "idFrom",
            jsonObject = buildJsonObject { put("x", JsonPrimitive("y")) }
        )

        val expected = buildJsonObject { put("assembled", JsonPrimitive("ok")) }

        everySuspend { materialDatabaseSource.getRootJsonObjectEntityOrNull(url) } returns entity
        everySuspend { materialDatabaseSource.getAllCommonRefOrNull(any(), any(), any()) } returns null
        everySuspend {
            materialAssembler.process(
                materialObject = entity.jsonObject,
                findAllRefOrNullFetcher = any()
            )
        } calls { args ->
            val block = args.arg(1) as FindAllRefOrNullFetcherProtocol
            block.invoke(from, type)
            expected
        }

        val res = sut.assemble(url)
        assertEquals(expected, res)

        verifySuspend(exhaustiveOrder) {
            coroutineTestScope.mock.database.await<Any>(any())
            materialDatabaseSource.getRootJsonObjectEntityOrNull(url)
            coroutineTestScope.mock.parser.await<Any>(any())
            materialAssembler.process(entity.jsonObject, any())
            coroutineTestScope.mock.database.await<Any>(any())
            materialDatabaseSource.getAllCommonRefOrNull(from, url, type)
        }
    }
}
