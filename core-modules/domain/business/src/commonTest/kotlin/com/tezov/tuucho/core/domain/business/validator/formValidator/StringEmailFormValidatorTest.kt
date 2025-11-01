package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlinx.serialization.json.JsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringEmailFormValidatorTest {
    private lateinit var sut: StringEmailFormValidator

    @BeforeTest
    fun setUp() {
        sut = StringEmailFormValidator(JsonObject(emptyMap()))
    }

    @Test
    fun `empty string is valid`() {
        sut.updateValidity("")
        assertTrue(sut.isValid)
    }

    @Test
    fun `valid simple email is valid`() {
        sut.updateValidity("user@example.com")
        assertTrue(sut.isValid)
    }

    @Test
    fun `valid email with plus and dot is valid`() {
        sut.updateValidity("first.last+tag@example.co.uk")
        assertTrue(sut.isValid)
    }

    @Test
    fun `missing at sign is invalid`() {
        sut.updateValidity("user.example.com")
        assertFalse(sut.isValid)
    }

    @Test
    fun `missing domain part is invalid`() {
        sut.updateValidity("user@")
        assertFalse(sut.isValid)
    }

    @Test
    fun `missing local part is invalid`() {
        sut.updateValidity("@example.com")
        assertFalse(sut.isValid)
    }

    @Test
    fun `spaces are invalid`() {
        sut.updateValidity("user @example.com")
        assertFalse(sut.isValid)
    }
}
