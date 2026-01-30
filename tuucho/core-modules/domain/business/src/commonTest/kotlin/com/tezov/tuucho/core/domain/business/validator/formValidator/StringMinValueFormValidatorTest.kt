package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringMinValueFormValidatorTest {
    private lateinit var sut: StringMinValueFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringMinValueFormValidator("error-messages-id", minValue = 10)
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
    fun `number greater than minValue is valid`() {
        sut.updateValidity("11")
        assertTrue(sut.isValid)
    }

    @Test
    fun `number equal to minValue is invalid`() {
        sut.updateValidity("10")
        assertFalse(sut.isValid)
    }

    @Test
    fun `number less than minValue is invalid`() {
        sut.updateValidity("5")
        assertFalse(sut.isValid)
    }

    @Test
    fun `non numeric string is invalid`() {
        sut.updateValidity("abc")
        assertFalse(sut.isValid)
    }
}
