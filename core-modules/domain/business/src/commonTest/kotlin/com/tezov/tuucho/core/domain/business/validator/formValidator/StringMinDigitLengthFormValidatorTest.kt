package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringMinDigitLengthFormValidatorTest {
    private lateinit var sut: StringMinDigitLengthFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringMinDigitLengthFormValidator(JsonObject(emptyMap()), length = 3)
    }

    @Test
    fun `empty string is invalid`() {
        sut.updateValidity("")
        assertFalse(sut.isValid)
    }

    @Test
    fun `string with fewer digits than required is invalid`() {
        sut.updateValidity("a1b2")
        assertFalse(sut.isValid) // only 2 digits
    }

    @Test
    fun `string with exactly required digits is valid`() {
        sut.updateValidity("abc123")
        assertTrue(sut.isValid) // 3 digits
    }

    @Test
    fun `string with more than required digits is valid`() {
        sut.updateValidity("1a2b3c4")
        assertTrue(sut.isValid) // 4 digits
    }

    @Test
    fun `string with no digits is invalid`() {
        sut.updateValidity("abcdef")
        assertFalse(sut.isValid)
    }
}
