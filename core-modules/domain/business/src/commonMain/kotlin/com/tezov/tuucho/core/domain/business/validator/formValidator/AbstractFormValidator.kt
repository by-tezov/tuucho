package com.tezov.tuucho.core.domain.business.validator.formValidator

import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol

abstract class AbstractFormValidator<T : Any>(
    override val errorMessagesId: String?,
) : FormValidatorProtocol<T> {
    override var isValid: Boolean = false
        protected set
}
