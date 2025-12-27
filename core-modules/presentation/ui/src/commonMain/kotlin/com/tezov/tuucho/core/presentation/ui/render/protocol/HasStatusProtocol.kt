package com.tezov.tuucho.core.presentation.ui.render.protocol

interface HasStatusProtocol {
    val isReady: Boolean

    var onStatusChanged: () -> Unit
}
