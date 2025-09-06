package com.tezov.tuucho.core.domain.tool.extension

import com.tezov.tuucho.core.domain.tool.extension.ExtensionInt.action
import com.tezov.tuucho.core.domain.tool.extension.ExtensionInt.isEven
import com.tezov.tuucho.core.domain.tool.extension.ExtensionInt.isOdd
import com.tezov.tuucho.core.domain.tool.extension.ExtensionInt.onEven
import com.tezov.tuucho.core.domain.tool.extension.ExtensionInt.onOdd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExtensionIntTest {

    @Test
    fun `isEven returns true for even numbers`() {
        assertTrue(2.isEven())
        assertTrue(0.isEven())
        assertTrue((-4).isEven())
    }

    @Test
    fun `isEven returns false for odd numbers`() {
        assertFalse(1.isEven())
        assertFalse((-3).isEven())
    }

    @Test
    fun `onEven executes block for even number`() {
        val result = 4.onEven { it * 2 }
        assertEquals(8, result)
    }

    @Test
    fun `onEven returns null for odd number`() {
        val result = 5.onEven { it * 2 }
        assertNull(result)
    }

    @Test
    fun `isOdd returns true for odd numbers`() {
        assertTrue(1.isOdd())
        assertTrue((-3).isOdd())
    }

    @Test
    fun `isOdd returns false for even numbers`() {
        assertFalse(2.isOdd())
        assertFalse(0.isOdd())
    }

    @Test
    fun `onOdd executes block for odd number`() {
        val result = 7.onOdd { it + 1 }
        assertEquals(8, result)
    }

    @Test
    fun `onOdd returns null for even number`() {
        val result = 6.onOdd { it + 1 }
        assertNull(result)
    }

    @Test
    fun `action executes ifEven block for even number`() {
        val result = 8.action(
            ifEven = { it / 2 },
            ifOdd = { -1 }
        )
        assertEquals(4, result)
    }

    @Test
    fun `action executes ifOdd block for odd number`() {
        val result = 9.action(
            ifEven = { -1 },
            ifOdd = { it * 3 }
        )
        assertEquals(27, result)
    }
}
