package com.tezov.tuucho.core.domain.business.protocol.screen.view.form

import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol

interface FormViewProtocol {

    interface Extension<F : FormViewProtocol>: ViewProtocol {
        val formView: F
    }

    fun attach(view: ViewProtocol)

    fun updateValidity()

    fun isValid(): Boolean?

    fun getId(): String

    fun getValue(): String
}