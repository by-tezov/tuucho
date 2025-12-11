package com.tezov.tuucho.core.presentation.ui.render.protocol.projector

import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProcessorProtocol

interface MessageProcessorProjectorProtocol : ProcessorProjectorProtocol {
    val subset: String

    fun add(
        projection: ProjectionProcessorProtocol
    )
}
