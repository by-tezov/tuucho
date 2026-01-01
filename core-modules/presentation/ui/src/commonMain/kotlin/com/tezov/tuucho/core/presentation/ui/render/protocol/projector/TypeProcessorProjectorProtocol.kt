package com.tezov.tuucho.core.presentation.ui.render.protocol.projector

import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProcessorProtocol

interface TypeProcessorProjectorProtocol : ProcessorProjectorProtocol {
    fun add(
        projection: ProjectionProcessorProtocol
    )
}
