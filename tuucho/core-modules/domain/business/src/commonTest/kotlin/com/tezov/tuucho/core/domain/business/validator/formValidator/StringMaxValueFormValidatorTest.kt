package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringMaxValueFormValidatorTest {
    private lateinit var sut: StringMaxValueFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringMaxValueFormValidator("error-messages-id", maxValue = 10)
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
    fun `number less than maxValue is valid`() {
        sut.updateValidity("5")
        assertTrue(sut.isValid)
    }

    @Test
    fun `number equal to maxValue is invalid`() {
        sut.updateValidity("10")
        assertFalse(sut.isValid)
    }

    @Test
    fun `number greater than maxValue is invalid`() {
        sut.updateValidity("15")
        assertFalse(sut.isValid)
    }

    @Test
    fun `non numeric string is invalid`() {
        sut.updateValidity("abc")
        assertFalse(sut.isValid)
    }
}
