package com.tezov.tuucho.core.domain.business.validator.formValidator

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class DummyFormValidator(
    errorMessages: JsonObject
) : AbstractFormValidator<String>(errorMessages) {
    override fun updateValidity(
        value: String
    ) {
        isValid = value == "ok"
    }
}

class AbstractFormValidatorTest {
    private val defaultMessage = "Default message"
    private val frenchMessage = "message en français"

    private val errorMessages = JsonObject(
        mapOf(
            "default" to JsonPrimitive(defaultMessage),
            "fr" to JsonPrimitive(frenchMessage)
        )
    )

    private lateinit var sut: DummyFormValidator

    @BeforeTest
    fun setUp() {
        sut = DummyFormValidator(errorMessages)
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

    @Test
    fun `getErrorMessage returns matching language`() {
        val msg = sut.getErrorMessage(LanguageModelDomain.French)
        assertEquals(frenchMessage, msg)
    }

    @Test
    fun `error message falls back to default when language not present`() {
        val sutNoLanguage = StringOnlyDigitsFormValidator(
            JsonObject(mapOf("default" to JsonPrimitive(defaultMessage)))
        )
        val msg = sutNoLanguage.getErrorMessage(LanguageModelDomain.from("fr"))
        assertEquals(defaultMessage, msg)
    }

    @Test
    fun `error message empty string when neither language nor default present`() {
        val sutNoDefault = StringOnlyDigitsFormValidator(
            JsonObject(mapOf("en" to JsonPrimitive("Other error")))
        )
        val msg = sutNoDefault.getErrorMessage(LanguageModelDomain.from("fr"))
        assertEquals("", msg)
    }
}
