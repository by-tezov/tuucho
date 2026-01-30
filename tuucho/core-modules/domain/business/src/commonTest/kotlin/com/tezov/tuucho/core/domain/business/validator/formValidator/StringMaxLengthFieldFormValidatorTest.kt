package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringMaxLengthFieldFormValidatorTest {
    private lateinit var sut: StringMaxLengthFieldFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringMaxLengthFieldFormValidator("error-messages-id", length = 3)
    }

    @Test
    fun `empty string is valid`() {
        sut.updateValidity("")
        assertTrue(sut.isValid)
    }

    @Test
    fun `null string is valid`() {
        sut.updateValidity(null)
        assertTrue(sut.isValid)
    }

    @Test
    fun `string shorter than max length is valid`() {
        sut.updateValidity("ab")
        assertTrue(sut.isValid)
    }

    @Test
    fun `string equal to max length is valid`() {
        sut.updateValidity("abc")
        assertTrue(sut.isValid)
    }

    @Test
    fun `string longer than max length is invalid`() {
        sut.updateValidity("abcd")
        assertFalse(sut.isValid)
    }

    @Test
    fun `value is null and length is above 0 is valid`() {
        val sut = StringMaxLengthFieldFormValidator("error-messages-id", length = 1)

        sut.updateValidity(null)
        assertTrue(sut.isValid)
    }

    @Test
    fun `value is null and length is 0 is invalid`() {
        val sut = StringMaxLengthFieldFormValidator("error-messages-id", length = 0)

        sut.updateValidity(null)
        assertFalse(sut.isValid)
    }
}
