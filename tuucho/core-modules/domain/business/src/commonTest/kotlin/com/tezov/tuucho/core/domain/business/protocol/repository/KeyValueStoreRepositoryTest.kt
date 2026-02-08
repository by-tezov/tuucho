package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import kotlin.test.Test
import kotlin.test.assertNotEquals

class KeyValueStoreRepositoryTest {
    @Test
    fun `key inequality different value`() {
        val keyA = "abc".toKey()
        val keyB = "xyz".toKey()

        assertNotEquals(keyA, keyB)
        assertNotEquals(keyA.hashCode(), keyB.hashCode())
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
