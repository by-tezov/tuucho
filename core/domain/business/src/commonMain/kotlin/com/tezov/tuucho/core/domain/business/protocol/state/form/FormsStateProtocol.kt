package com.tezov.tuucho.core.domain.business.protocol.state.form

interface FormsStateProtocol : FormStateProtocol {

    override fun clear() {
        fields().clear()
    }

    fun fields(): FieldsFormStateProtocol

}

