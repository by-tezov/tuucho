package com.tezov.tuucho.core.domain.business.protocol.screen.view.form

import com.tezov.tuucho.core.domain.business.protocol.screen.FieldValidatorProtocol

interface FieldFormViewProtocol : FormViewProtocol {

    interface Extension : FormViewProtocol.Extension<FieldFormViewProtocol> {
        override val formView: FieldFormViewProtocol
    }

    val validators: List<FieldValidatorProtocol<String>>?

}