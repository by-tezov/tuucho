package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringOnlyDigitsFormValidatorTest {
    private lateinit var sut: StringOnlyDigitsFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringOnlyDigitsFormValidator("error-messages-id")
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
    fun `string with only digits is valid`() {
        sut.updateValidity("123456")
        assertTrue(sut.isValid)
    }

    @Test
    fun `string with non digit is invalid`() {
        sut.updateValidity("12a34")
        assertFalse(sut.isValid)
    }

    @Test
    fun `string with only letters is invalid`() {
        sut.updateValidity("abcdef")
        assertFalse(sut.isValid)
    }
}
