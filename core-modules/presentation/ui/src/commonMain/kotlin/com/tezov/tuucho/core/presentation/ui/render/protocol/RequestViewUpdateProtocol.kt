package com.tezov.tuucho.core.presentation.ui.render.protocol

fun interface RequestViewUpdateInvokerProtocol {
    fun invokeRequestViewUpdate()
}

interface RequestViewUpdateSetterProtocol {
    fun setRequestViewUpdater(value: RequestViewUpdateInvokerProtocol)
}

interface HasRequestViewUpdateProtocol {
    val requestViewUpdateInvoker: RequestViewUpdateInvokerProtocol?
}

interface RequestViewUpdateProtocols : HasRequestViewUpdateProtocol, RequestViewUpdateSetterProtocol, RequestViewUpdateInvokerProtocol
