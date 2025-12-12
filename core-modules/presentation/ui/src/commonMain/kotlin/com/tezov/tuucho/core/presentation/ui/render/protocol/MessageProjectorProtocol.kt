package com.tezov.tuucho.core.presentation.ui.render.protocol

interface MessageProjectorProtocol : ProjectorProtocol {

    val subset: String

    fun add(projectable: ProjectableProtocol)

}
