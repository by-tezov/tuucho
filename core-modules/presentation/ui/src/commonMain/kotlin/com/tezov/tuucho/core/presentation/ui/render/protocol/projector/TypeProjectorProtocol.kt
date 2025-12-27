package com.tezov.tuucho.core.presentation.ui.render.protocol.projector

import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol

interface TypeProjectorProtocol : ProjectorProtocol, HasUpdatableProtocol {
    fun add(
        projectable: ProjectableProtocol
    )
}
