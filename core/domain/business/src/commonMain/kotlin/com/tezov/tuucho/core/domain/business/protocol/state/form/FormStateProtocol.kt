package com.tezov.tuucho.core.domain.business.protocol.state.form

import kotlinx.serialization.json.JsonObject

interface FormStateProtocol {

    fun clear()

    fun updateAllValidity()

    fun isAllValid(): Boolean

    fun updateValidity(id: String)

    fun isValid(id: String): Boolean?

    fun getAllValidityResult(): List<Pair<String, Boolean>>

    fun data(): JsonObject
}