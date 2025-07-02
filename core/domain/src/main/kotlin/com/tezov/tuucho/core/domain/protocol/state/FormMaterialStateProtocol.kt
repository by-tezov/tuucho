package com.tezov.tuucho.core.domain.protocol.state

import kotlinx.serialization.json.JsonElement

interface FormMaterialStateProtocol {

    fun clear(){
        fieldsState().clear()
    }

    fun fieldsState(): FieldsMaterialStateProtocol

    fun isAllValid(): Boolean

    fun isValid(id: String): Boolean?

    fun data(): JsonElement
}