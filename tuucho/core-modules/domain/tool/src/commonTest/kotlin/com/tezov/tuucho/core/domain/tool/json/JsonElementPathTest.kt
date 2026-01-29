package com.tezov.tuucho.core.domain.tool.json

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonElementPathTest {
    @Test
    fun `empty string becomes empty path`() {
        val path = JsonElementPath("")
        assertTrue(path.isEmpty())
        assertEquals("", path.toString())
    }

    @Test
    fun `null string becomes empty path`() {
        val path = JsonElementPath(null)
        assertTrue(path.isEmpty())
        assertEquals("", path.toString())
    }

    @Test
    fun `leading and trailing separators are trimmed`() {
        val path = JsonElementPath("/a/b/c/")
        assertEquals("a/b/c", path.toString())
    }

    @Test
    fun `multiple separators collapse into one`() {
        val path = JsonElementPath("a//b///c")
        assertEquals("a/b/c", path.toString())
    }

    @Test
    fun `isEmpty true when no path`() {
        assertTrue(JsonElementPath("").isEmpty())
        assertTrue(JsonElementPath(null).isEmpty())
    }

    @Test
    fun `isEmpty false when path exists`() {
        assertFalse(JsonElementPath("a").isEmpty())
    }

    @Test
    fun `lastSegment returns last token`() {
        assertEquals("c", JsonElementPath("a/b/c").lastSegment())
    }

    @Test
    fun `lastSegment null for empty path`() {
        assertNull(JsonElementPath("").lastSegment())
    }

    @Test
    fun `child appends new segment`() {
        val path = JsonElementPath("a/b").child("c")
        assertEquals("a/b/c", path.toString())
    }

    @Test
    fun `child with empty string does nothing`() {
        val path = JsonElementPath("a/b").child("")
        assertEquals("a/b", path.toString())
    }

    @Test
    fun `child on empty path creates new segment`() {
        val path = JsonElementPath("").child("c")
        assertEquals("c", path.toString())
    }

    @Test
    fun `parent returns everything before last separator`() {
        val path = JsonElementPath("a/b/c").parent()
        assertEquals("a/b", path.toString())
    }

    @Test
    fun `parent of single segment becomes empty`() {
        val path = JsonElementPath("a").parent()
        assertTrue(path.isEmpty())
    }

    @Test
    fun `parent of empty stays empty`() {
        val path = JsonElementPath("").parent()
        assertTrue(path.isEmpty())
    }

    @Test
    fun `iterator yields all segments`() {
        val segments = JsonElementPath("a/b/c").toList()
        assertEquals(listOf("a", "b", "c"), segments)
    }

    @Test
    fun `iterator yields empty list for empty path`() {
        val segments = JsonElementPath("").toList()
        assertTrue(segments.isEmpty())
    }

    @Test
    fun `toString returns normalized path`() {
        val path = JsonElementPath("/a//b/c/")
        assertEquals("a/b/c", path.toString())
    }

    @Test
    fun `toPath creates path from string`() {
        val path = "a/b/c".toPath()
        assertEquals("a/b/c", path.toString())
    }

    @Test
    fun `toPath appends index when provided`() {
        val path = "tags".toPath(index = 1)
        assertEquals("tags#1", path.toString())
    }

    @Test
    fun `toPath with empty string and index creates index-only path`() {
        val path = "".toPath(index = 2)
        assertEquals("#2", path.toString())
    }

    @Test
    fun `toIndexPath creates index segment`() {
        val segment = 3.toIndexPath()
        assertEquals("#3", segment)
    }

    @Test
    fun `atIndex appends index to existing path`() {
        val path = JsonElementPath("tags")
        val indexed = path.atIndex(2)
        assertEquals("tags#2", indexed.toString())
    }

    @Test
    fun `atIndex with empty base path returns index-only path`() {
        val path = JsonElementPath("")
        val indexed = path.atIndex(5)
        assertEquals("#5", indexed.toString())
    }

    @Test
    fun `toPath appends index to empty string`() {
        val path = "".toPath(3)
        assertEquals("#3", path.toString())
    }

    @Test
    fun `toPath appends index to non-empty string`() {
        val path = "tags".toPath(1)
        assertEquals("tags#1", path.toString())
    }

    @Test
    fun `parent removes index from last segment`() {
        val path = JsonElementPath("tags#3")
        val parent = path.parent()
        assertEquals("tags", parent.toString())  // covers last.contains(INDEX_SEPARATOR)
    }
}
