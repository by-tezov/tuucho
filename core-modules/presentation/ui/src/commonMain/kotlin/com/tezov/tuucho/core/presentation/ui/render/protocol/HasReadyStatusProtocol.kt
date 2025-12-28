package com.tezov.tuucho.core.presentation.ui.render.protocol

const val defaultStatus = true

interface HasReadyStatusProtocol {
    val isReady: Boolean

    var onStatusChanged: () -> Unit
}
