package com.tezov.tuucho.core.domain.business.protocol.screen.view

interface FormStateProtocol {
    interface Extension : ViewProtocol {
        val extensionFormState: FormStateProtocol
    }

    fun updateValidity()

    fun isValid(): Boolean?

    fun getId(): String

    fun getValue(): String?
}
