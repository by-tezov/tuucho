package com.tezov.tuucho.core.domain.protocol.state

interface MaterialStateProtocol {

    fun clear(){
        form().clear()
    }

    fun form(): FormMaterialStateProtocol
}