package com.tezov.tuucho.core.presentation.ui.render.protocol.projector

import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol

interface TypeProjectorProtocol : ProjectorProtocol {
    fun add(
        projectable: ProjectableProtocol
    )
}
