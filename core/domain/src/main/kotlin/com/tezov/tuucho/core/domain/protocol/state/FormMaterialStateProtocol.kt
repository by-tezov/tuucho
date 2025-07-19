package com.tezov.tuucho.core.domain.protocol.state

import kotlinx.serialization.json.JsonElement

interface FormMaterialStateProtocol {

    fun clear(){
        fieldsState().clear()
    }

    fun fieldsState(): FieldsMaterialStateProtocol

    fun updateAllValidity()

    fun isAllValid(): Boolean

    fun updateValidity(id: String)

    fun isValid(id: String): Boolean?

    fun getAllValidityResult(): List<Pair<String, Boolean>>

    fun data(): JsonElement
}