package com.tezov.tuucho.core.presentation.ui.render.protocol

// TODO: not scalable, check the TODO to do something better. Will do the job for now

fun interface ReadyStatusInvalidateInvokerProtocol {
    fun invalidateReadyStatus()
}

interface ReadyStatusInvalidateInvokerSetterProtocol {
    fun setReadyStatusInvalidateInvoker(
        value: ReadyStatusInvalidateInvokerProtocol
    )
}

interface HasReadyStatusInvalidateInvokerProtocol {
    val readyStatusInvalidateInvoker: ReadyStatusInvalidateInvokerProtocol?
}

interface ReadyStatusInvalidateProtocols :
    HasReadyStatusInvalidateInvokerProtocol,
    ReadyStatusInvalidateInvokerSetterProtocol,
    ReadyStatusInvalidateInvokerProtocol
