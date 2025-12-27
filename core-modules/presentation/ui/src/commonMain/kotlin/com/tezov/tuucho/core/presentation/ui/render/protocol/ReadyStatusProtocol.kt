package com.tezov.tuucho.core.presentation.ui.render.protocol

interface ReadyStatusProtocol {
    val isReady: Boolean

    var onStatusChanged: () -> Unit
}
