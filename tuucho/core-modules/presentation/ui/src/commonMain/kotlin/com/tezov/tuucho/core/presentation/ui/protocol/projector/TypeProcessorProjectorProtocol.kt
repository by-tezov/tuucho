package com.tezov.tuucho.core.presentation.ui.protocol.projector

import com.tezov.tuucho.core.presentation.ui.protocol.ProjectionProcessorProtocol

interface TypeProcessorProjectorProtocol : ProcessorProjectorProtocol {
    fun add(
        projection: ProjectionProcessorProtocol
    )
}
