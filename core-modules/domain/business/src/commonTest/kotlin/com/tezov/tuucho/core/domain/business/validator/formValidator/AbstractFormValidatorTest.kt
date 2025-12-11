package com.tezov.tuucho.core.domain.business.validator.formValidator

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class DummyFormValidator(
    errorMessagesId: String?
) : AbstractFormValidator<String>(errorMessagesId) {
    override fun updateValidity(
        value: String?
    ) {
        isValid = value == "ok"
    }
}

class AbstractFormValidatorTest {
    private val errorMessagesId = "error-messages-id"

    private lateinit var sut: DummyFormValidator

    @BeforeTest
    fun setUp() {
        sut = DummyFormValidator(errorMessagesId)
    }

    @Test
    fun `isValid returns false by default`() {
        assertFalse(sut.isValid)
    }

    @Test
    fun `updateValidity sets isValid true when condition met`() {
        sut.updateValidity("ok")
        assertTrue(sut.isValid)
    }

    @Test
    fun `updateValidity sets isValid false when condition not met`() {
        sut.updateValidity("not ok")
        assertFalse(sut.isValid)
    }
}
