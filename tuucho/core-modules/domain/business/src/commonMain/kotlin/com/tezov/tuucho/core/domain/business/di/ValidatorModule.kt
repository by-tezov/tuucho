package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringEmailFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMaxLengthFieldFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMaxValueFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMinDigitLengthFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMinLengthFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringMinValueFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringNotNullFormValidator
import com.tezov.tuucho.core.domain.business.validator.formValidator.StringOnlyDigitsFormValidator
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal object ValidatorModule {
    fun invoke() = module(ModuleContextDomain.Validator) {
        factoryOf(StringEmailFormValidator::Factory) bind FormValidatorProtocol.Factory::class
        factoryOf(StringMaxLengthFieldFormValidator::Factory) bind FormValidatorProtocol.Factory::class
        factoryOf(StringMaxValueFormValidator::Factory) bind FormValidatorProtocol.Factory::class
        factoryOf(StringMinDigitLengthFormValidator::Factory) bind FormValidatorProtocol.Factory::class
        factoryOf(StringMinLengthFormValidator::Factory) bind FormValidatorProtocol.Factory::class
        factoryOf(StringMinValueFormValidator::Factory) bind FormValidatorProtocol.Factory::class
        factoryOf(StringNotNullFormValidator::Factory) bind FormValidatorProtocol.Factory::class
        factoryOf(StringOnlyDigitsFormValidator::Factory) bind FormValidatorProtocol.Factory::class
    }
}
