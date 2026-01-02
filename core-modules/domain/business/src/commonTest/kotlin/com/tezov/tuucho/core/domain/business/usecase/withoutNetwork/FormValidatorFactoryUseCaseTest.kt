package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormValidatorSchema.Value.Type
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringEmailFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMaxLengthFieldFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMaxValueFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMinDigitLengthFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMinLengthFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMinValueFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringNotNullFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringOnlyDigitsFormValidator
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class FormValidatorFactoryUseCaseTest {
    private lateinit var sut: FormValidatorFactoryUseCase

    @BeforeTest
    fun setup() {
        sut = FormValidatorFactoryUseCase()
    }

    private fun prototype(
        type: String,
        length: String? = null,
        value: String? = null
    ) = FormValidatorFactoryUseCase.Input(
        prototypeObject = buildJsonObject {
            put(FormValidatorSchema.Key.type, type)
            length?.let { put(FormValidatorSchema.Key.length, it) }
            value?.let { put(FormValidatorSchema.Key.value, it) }
            put(FormValidatorSchema.Key.messageErrors, buildJsonObject { put("msg", "err") })
        }
    )

    @Test
    fun `string min length creates StringMinLengthFormValidator`() {
        val input = prototype(type = Type.stringMinLength, length = "5")
        val output = sut.invoke(input)
        assertIs<StringMinLengthFormValidator>(output.validator)
    }

    @Test
    fun `string max length creates StringMaxLengthFieldFormValidator`() {
        val input = prototype(type = Type.stringMaxLength, length = "10")
        val output = sut.invoke(input)
        assertIs<StringMaxLengthFieldFormValidator>(output.validator)
    }

    @Test
    fun `string min digit length creates StringMinDigitLengthFormValidator`() {
        val input = prototype(type = Type.stringMinDigitLength, length = "3")
        val output = sut.invoke(input)
        assertIs<StringMinDigitLengthFormValidator>(output.validator)
    }

    @Test
    fun `string only digits creates StringOnlyDigitsFormValidator`() {
        val input = prototype(type = Type.stringOnlyDigits)
        val output = sut.invoke(input)
        assertIs<StringOnlyDigitsFormValidator>(output.validator)
    }

    @Test
    fun `string email creates StringEmailFormValidator`() {
        val input = prototype(type = Type.stringEmail)
        val output = sut.invoke(input)
        assertIs<StringEmailFormValidator>(output.validator)
    }

    @Test
    fun `string not null creates StringNotNullFormValidator`() {
        val input = prototype(type = Type.stringNotNull)
        val output = sut.invoke(input)
        assertIs<StringNotNullFormValidator>(output.validator)
    }

    @Test
    fun `string min value creates StringMinValueFormValidator`() {
        val input = prototype(type = Type.stringMinValue, value = "2")
        val output = sut.invoke(input)
        assertIs<StringMinValueFormValidator>(output.validator)
    }

    @Test
    fun `string max value creates StringMaxValueFormValidator`() {
        val input = prototype(type = Type.stringMaxValue, value = "12")
        val output = sut.invoke(input)
        assertIs<StringMaxValueFormValidator>(output.validator)
    }

    @Test
    fun `invalid type throws DomainException`() {
        val input = prototype(type = "unknown")
        assertFailsWith<DomainException.Default> {
            sut.invoke(input)
        }
    }
}
