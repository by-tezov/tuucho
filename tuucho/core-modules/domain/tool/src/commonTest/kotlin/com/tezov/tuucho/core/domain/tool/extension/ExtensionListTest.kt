package com.tezov.tuucho.core.domain.tool.extension

import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.isNotNullIndex
import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.isNullIndex
import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.pop
import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.popOrNull
import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.priorLast
import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.priorLastOrNull
import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.push
import com.tezov.tuucho.core.domain.tool.extension.ExtensionList.subListToEnd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExtensionListTest {
    @Test
    fun `isNullIndex returns true when equals NULL_INDEX`() {
        assertTrue((-1).isNullIndex)
        assertFalse(0.isNullIndex)
        assertFalse(5.isNullIndex)
    }

    @Test
    fun `isNotNullIndex returns true when not equals NULL_INDEX`() {
        assertFalse((-1).isNotNullIndex)
        assertTrue(0.isNotNullIndex)
        assertTrue(10.isNotNullIndex)
    }

    @Test
    fun `priorLast returns second to last element`() {
        val list = listOf(1, 2, 3, 4)
        assertEquals(3, list.priorLast())
    }

    @Test
    fun `priorLast throws when list has fewer than 2 elements`() {
        assertFailsWith<NoSuchElementException> {
            listOf(1).priorLast()
        }
        assertFailsWith<NoSuchElementException> {
            emptyList<Int>().priorLast()
        }
    }

    @Test
    fun `priorLastOrNull returns null when list has fewer than 2 elements`() {
        assertNull(listOf(1).priorLastOrNull())
        assertNull(emptyList<Int>().priorLastOrNull())
    }

    @Test
    fun `priorLastOrNull returns second to last element when available`() {
        val list = listOf("a", "b", "c")
        assertEquals("b", list.priorLastOrNull())
    }

    @Test
    fun `subListToEnd returns sublist from given index to end`() {
        val list = listOf("x", "y", "z", "w")
        assertEquals(listOf("z", "w"), list.subListToEnd(2))
    }

    @Test
    fun `subListToEnd with index 0 returns full list`() {
        val list = listOf(10, 20, 30)
        assertEquals(list, list.subListToEnd(0))
    }

    @Test
    fun `push adds element to end of ArrayDeque`() {
        val deque = ArrayDeque(listOf(1, 2))
        deque.push(3)
        assertEquals(listOf(1, 2, 3), deque.toList())
    }

    @Test
    fun `pop removes and returns last element`() {
        val deque = ArrayDeque(listOf("a", "b", "c"))
        val popped = deque.pop()
        assertEquals("c", popped)
        assertEquals(listOf("a", "b"), deque.toList())
    }

    @Test
    fun `pop throws when deque is empty`() {
        val deque = ArrayDeque<Int>()
        assertFailsWith<NoSuchElementException> {
            deque.pop()
        }
    }

    @Test
    fun `popOrNull returns last element when available`() {
        val deque = ArrayDeque(listOf(5, 6))
        val popped = deque.popOrNull()
        assertEquals(6, popped)
        assertEquals(listOf(5), deque.toList())
    }

    @Test
    fun `popOrNull returns null when empty`() {
        val deque = ArrayDeque<String>()
        assertNull(deque.popOrNull())
    }
}
