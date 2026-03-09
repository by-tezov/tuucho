package com.tezov.tuucho.core.presentation.ui.protocol.projector

import com.tezov.tuucho.core.presentation.ui.protocol.ProjectionProcessorProtocol

interface MessageProcessorProjectorProtocol : ProcessorProjectorProtocol {
    val subset: String

    fun add(
        projection: ProjectionProcessorProtocol
    )
}
