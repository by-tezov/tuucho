package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringMinLengthFormValidatorTest {
    private lateinit var sut: StringMinLengthFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringMinLengthFormValidator("error-messages-id", length = 3)
    }

    @Test
    fun `empty string is invalid`() {
        sut.updateValidity("")
        assertFalse(sut.isValid)
    }

    @Test
    fun `null string is invalid`() {
        sut.updateValidity(null)
        assertFalse(sut.isValid)
    }

    @Test
    fun `string shorter than required length is invalid`() {
        sut.updateValidity("ab")
        assertFalse(sut.isValid)
    }

    @Test
    fun `string with exact required length is valid`() {
        sut.updateValidity("abc")
        assertTrue(sut.isValid)
    }

    @Test
    fun `string longer than required length is valid`() {
        sut.updateValidity("abcd")
        assertTrue(sut.isValid)
    }

    @Test
    fun `value is null and length is 0 is valid`() {
        val sut = StringMinLengthFormValidator("error-messages-id", length = 0)

        sut.updateValidity(null)
        assertTrue(sut.isValid)
    }
}
