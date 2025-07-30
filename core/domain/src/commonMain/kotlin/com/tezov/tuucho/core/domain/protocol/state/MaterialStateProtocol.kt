package com.tezov.tuucho.core.domain.protocol.state

interface MaterialStateProtocol {

    var url: String

    fun clear(){
        form().clear()
    }

    fun form(): FormMaterialStateProtocol
}