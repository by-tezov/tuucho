package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringMaxLengthFieldFormValidatorTest {
    private lateinit var sut: StringMaxLengthFieldFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringMaxLengthFieldFormValidator(JsonObject(emptyMap()), length = 3)
    }

    @Test
    fun `empty string is valid`() {
        sut.updateValidity("")
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
}
