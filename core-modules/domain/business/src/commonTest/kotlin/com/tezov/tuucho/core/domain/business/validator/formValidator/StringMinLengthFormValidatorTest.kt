package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringMinLengthFormValidatorTest {

    private lateinit var sut: StringMinLengthFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringMinLengthFormValidator(JsonObject(emptyMap()), length = 3)
    }

    @Test
    fun `empty string is invalid`() {
        sut.updateValidity("")
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
}
