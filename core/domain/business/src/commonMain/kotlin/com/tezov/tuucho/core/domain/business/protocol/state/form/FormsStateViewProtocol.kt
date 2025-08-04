package com.tezov.tuucho.core.domain.business.protocol.state.form

interface FormsStateViewProtocol : FormStateViewProtocol {

    fun fields(): FieldsFormStateViewProtocol

}

