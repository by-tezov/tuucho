package com.tezov.tuucho.core.domain.protocol.state.form

interface FormMaterialStateProtocol: FormStateProtocol {

    override fun clear(){
        fieldsState().clear()
    }

    fun fieldsState(): FieldsMaterialStateProtocol

}

