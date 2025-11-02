package com.tezov.tuucho.core.domain.tool.extension

import com.tezov.tuucho.core.domain.tool.extension.ExtensionNull.isNotNull
import com.tezov.tuucho.core.domain.tool.extension.ExtensionNull.isNotNullAndNotEmpty
import com.tezov.tuucho.core.domain.tool.extension.ExtensionNull.isNull
import com.tezov.tuucho.core.domain.tool.extension.ExtensionNull.isNullOrEmpty
import com.tezov.tuucho.core.domain.tool.extension.ExtensionNull.nullify
import com.tezov.tuucho.core.domain.tool.extension.ExtensionNull.simpleName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ExtensionNullTest {
    @Test
    fun `isNull returns true for null`() {
        val obj: Any? = null
        assertTrue(obj.isNull)
    }

    @Test
    fun `isNull returns false for non null`() {
        val obj: Any? = "x"
        assertFalse(obj.isNull)
    }

    @Test
    fun `isNotNull returns true for non null`() {
        val obj: Any? = 123
        assertTrue(obj.isNotNull)
    }

    @Test
    fun `isNotNull returns false for null`() {
        val obj: Any? = null
        assertFalse(obj.isNotNull)
    }

    @Test
    fun `nullify returns same list if not empty`() {
        val list = mutableListOf(1, 2, 3)
        val result = list.nullify()
        assertSame(list, result)
    }

    @Test
    fun `nullify returns null for empty list`() {
        val list = mutableListOf<Int>()
        assertNull(list.nullify())
    }

    @Test
    fun `nullify returns null for null list`() {
        val list: MutableList<Int>? = null
        assertNull(list.nullify())
    }

    @Test
    fun `nullify returns same collection if not empty`() {
        val coll: Collection<Int>? = listOf(1, 2)
        val result = coll.nullify()
        assertSame(coll, result)
    }

    @Test
    fun `nullify returns null for empty collection`() {
        val coll: Collection<Int>? = emptyList()
        assertNull(coll.nullify())
    }

    @Test
    fun `string nullify returns same string if not empty`() {
        val s: String? = "abc"
        assertEquals("abc", s.nullify())
    }

    @Test
    fun `string nullify returns null if empty`() {
        val s: String? = ""
        assertNull(s.nullify())
    }

    @Test
    fun `string nullify returns null if null`() {
        val s: String? = null
        assertNull(s.nullify())
    }

    @Test
    fun `string isNullOrEmpty true for null or empty`() {
        assertTrue((null as String?).isNullOrEmpty())
        assertTrue("".isNullOrEmpty())
        assertFalse("a".isNullOrEmpty())
    }

    @Test
    fun `string isNotNullAndNotEmpty true only for non empty`() {
        assertFalse((null as String?).isNotNullAndNotEmpty())
        assertFalse("".isNotNullAndNotEmpty())
        assertTrue("x".isNotNullAndNotEmpty())
    }

    @Test
    fun `charSequence isNullOrEmpty true for null or empty`() {
        val cs: CharSequence? = null
        assertTrue(cs.isNullOrEmpty())
        assertTrue("".isNullOrEmpty())
        assertFalse("x".isNullOrEmpty())
    }

    @Test
    fun `charSequence isNotNullAndNotEmpty true only for non empty`() {
        val cs: CharSequence? = "hi"
        assertTrue(cs.isNotNullAndNotEmpty())
        assertFalse((null as CharSequence?).isNotNullAndNotEmpty())
        assertFalse("".isNotNullAndNotEmpty())
    }

    @Test
    fun `byteArray nullify overwrites with zeros`() {
        val arr = byteArrayOf(1, 2, 3)
        arr.nullify()
        assertTrue(arr.all { it == 0.toByte() })
    }

    @Test
    fun `byteArray isNullOrEmpty behaves correctly`() {
        assertTrue((null as ByteArray?).isNullOrEmpty())
        assertTrue(byteArrayOf().isNullOrEmpty())
        assertFalse(byteArrayOf(1).isNullOrEmpty())
    }

    @Test
    fun `byteArray isNotNullAndNotEmpty behaves correctly`() {
        assertFalse((null as ByteArray?).isNotNullAndNotEmpty())
        assertFalse(byteArrayOf().isNotNullAndNotEmpty())
        assertTrue(byteArrayOf(1).isNotNullAndNotEmpty())
    }

    @Test
    fun `uByteArray nullify overwrites with zeros`() {
        val arr = ubyteArrayOf(1u, 2u, 3u)
        arr.nullify()
        assertTrue(arr.all { it == 0.toUByte() })
    }

    @Test
    fun `uByteArray isNullOrEmpty behaves correctly`() {
        assertTrue((null as UByteArray?).isNullOrEmpty())
        assertTrue(ubyteArrayOf().isNullOrEmpty())
        assertFalse(ubyteArrayOf(1u).isNullOrEmpty())
    }

    @Test
    fun `uByteArray isNotNullAndNotEmpty behaves correctly`() {
        assertFalse((null as UByteArray?).isNotNullAndNotEmpty())
        assertFalse(ubyteArrayOf().isNotNullAndNotEmpty())
        assertTrue(ubyteArrayOf(1u).isNotNullAndNotEmpty())
    }

    @Test
    fun `charArray nullify overwrites with zeros`() {
        val arr = charArrayOf('a', 'b', 'c')
        arr.nullify()
        assertTrue(arr.all { it == 0.toChar() })
    }

    @Test
    fun `charArray isNullOrEmpty behaves correctly`() {
        assertTrue((null as CharArray?).isNullOrEmpty())
        assertTrue(charArrayOf().isNullOrEmpty())
        assertFalse(charArrayOf('x').isNullOrEmpty())
    }

    @Test
    fun `charArray isNotNullAndNotEmpty behaves correctly`() {
        assertFalse((null as CharArray?).isNotNullAndNotEmpty())
        assertFalse(charArrayOf().isNotNullAndNotEmpty())
        assertTrue(charArrayOf('x').isNotNullAndNotEmpty())
    }

    @Test
    fun `simpleName returns class name when not null`() {
        val obj: Any? = "string"
        assertEquals("String", obj.simpleName)
    }

    @Test
    fun `simpleName returns null for null`() {
        val obj: Any? = null
        assertNull(obj.simpleName)
    }
}
