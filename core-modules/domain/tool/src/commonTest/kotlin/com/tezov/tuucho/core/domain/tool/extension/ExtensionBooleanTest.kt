package com.tezov.tuucho.core.domain.tool.extension

import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.action
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.ifFalse
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.ifTrue
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isFalse
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isFalseOrNull
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrueOrNull
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.toInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExtensionBooleanTest {
    @Test
    fun `isTrue returns true only when true`() {
        assertTrue(true.isTrue)
        assertFalse(false.isTrue)
        assertFalse((null as Boolean?).isTrue)
    }

    @Test
    fun `isTrueOrNull returns true for true or null`() {
        assertTrue(true.isTrueOrNull)
        assertFalse(false.isTrueOrNull)
        assertTrue((null as Boolean?).isTrueOrNull)
    }

    @Test
    fun `isFalse returns true only when false`() {
        assertFalse(true.isFalse)
        assertTrue(false.isFalse)
        assertFalse((null as Boolean?).isFalse)
    }

    @Test
    fun `isFalseOrNull returns true for false or null`() {
        assertFalse(true.isFalseOrNull)
        assertTrue(false.isFalseOrNull)
        assertTrue((null as Boolean?).isFalseOrNull)
    }

    @Test
    fun `ifTrue executes block when true`() {
        val result = true.ifTrue { "executed" }
        assertEquals("executed", result)
    }

    @Test
    fun `ifTrue returns null when false`() {
        val result = false.ifTrue { "executed" }
        assertNull(result)
    }

    @Test
    fun `ifFalse executes block when false`() {
        val result = false.ifFalse { "executed" }
        assertEquals("executed", result)
    }

    @Test
    fun `ifFalse returns null when true`() {
        val result = true.ifFalse { "executed" }
        assertNull(result)
    }

    @Test
    fun `action executes ifTrue branch when true`() {
        val result = true.action(
            ifTrue = { "yes" },
            ifFalse = { "no" }
        )
        assertEquals("yes", result)
    }

    @Test
    fun `action executes ifFalse branch when false`() {
        val result = false.action(
            ifTrue = { "yes" },
            ifFalse = { "no" }
        )
        assertEquals("no", result)
    }

    @Test
    fun `toInt returns 1 for true`() {
        assertEquals(1, true.toInt())
    }

    @Test
    fun `toInt returns 0 for false`() {
        assertEquals(0, false.toInt())
    }

    @Test
    fun `nullable toInt returns 1 for true`() {
        assertEquals(1, true.toInt())
    }

    @Test
    fun `nullable toInt returns 0 for false`() {
        assertEquals(0, false.toInt())
    }

    @Test
    fun `nullable toInt returns null for null`() {
        assertNull((null as Boolean?).toInt())
    }

    @Test
    fun `ifTrue executes block only when value is true`() {
        val executed = true.ifTrue { "ok" }
        assertEquals("ok", executed)
    }

    @Test
    fun `ifTrue returns null when value is false`() {
        val executed = false.ifTrue { "should not run" }
        assertNull(executed)
    }

    @Test
    fun `ifFalse executes block only when value is false`() {
        val executed = false.ifFalse { "ok" }
        assertEquals("ok", executed)
    }

    @Test
    fun `ifFalse returns null when value is true`() {
        val executed = true.ifFalse { "should not run" }
        assertNull(executed)
    }

    @Test
    fun `action selects ifTrue branch when value is true`() {
        val result = true.action(
            ifTrue = { "true-branch" },
            ifFalse = { "false-branch" }
        )
        assertEquals("true-branch", result)
    }

    @Test
    fun `action selects ifFalse branch when value is false`() {
        val result = false.action(
            ifTrue = { "true-branch" },
            ifFalse = { "false-branch" }
        )
        assertEquals("false-branch", result)
    }
}
