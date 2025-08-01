package com.tezov.tuucho.core.domain.protocol.state.form

interface FormsStateProtocol: FormStateProtocol {

    override fun clear(){
        fields().clear()
    }

    fun fields(): FieldsFormStateProtocol

}

