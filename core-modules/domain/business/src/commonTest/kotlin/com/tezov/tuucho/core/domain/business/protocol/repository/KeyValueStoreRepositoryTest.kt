package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class KeyValueStoreRepositoryTest {

    @Test
    fun `key equality same value`() {
        val keyA = "abc".toKey()
        val keyB = "abc".toKey()

        assertEquals(keyA, keyB)
        assertEquals(keyA.hashCode(), keyB.hashCode())
        assertEquals("Key(abc)", keyA.toString())
    }

    @Test
    fun `key inequality different value`() {
        val keyA = "abc".toKey()
        val keyB = "xyz".toKey()

        assertNotEquals(keyA, keyB)
        assertNotEquals(keyA.hashCode(), keyB.hashCode())
    }

    @Test
    fun `value equality same value`() {
        val valueA = "123".toValue()
        val valueB = "123".toValue()

        assertEquals(valueA, valueB)
        assertEquals(valueA.hashCode(), valueB.hashCode())
        assertEquals("Value(123)", valueA.toString())
    }

    @Test
    fun `value inequality different value`() {
        val valueA = "123".toValue()
        val valueB = "999".toValue()

        assertNotEquals(valueA, valueB)
        assertNotEquals(valueA.hashCode(), valueB.hashCode())
    }

    @Test
    fun `key does not equal value`() {
        val key = "abc".toKey()
        val value = "abc".toValue()

        assertNotEquals(key as Any?, value as Any?)
    }
}
