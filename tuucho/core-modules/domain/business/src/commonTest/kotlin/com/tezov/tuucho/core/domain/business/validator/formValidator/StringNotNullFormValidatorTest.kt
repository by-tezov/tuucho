package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringNotNullFormValidatorTest {
    private lateinit var sut: StringNotNullFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringNotNullFormValidator("error-messages-id")
    }

    @Test
    fun `empty string is invalid`() {
        sut.updateValidity("")
        assertFalse(sut.isValid)
    }

    @Test
    fun `non empty string is valid`() {
        sut.updateValidity("hello")
        assertTrue(sut.isValid)
    }
}
