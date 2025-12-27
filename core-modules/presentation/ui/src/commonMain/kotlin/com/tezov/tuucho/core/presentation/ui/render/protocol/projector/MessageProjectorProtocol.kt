package com.tezov.tuucho.core.presentation.ui.render.protocol.projector

import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol

interface MessageProjectorProtocol : ProjectorProtocol {
    val subset: String

    fun add(
        projectable: ProjectableProtocol
    )
}
