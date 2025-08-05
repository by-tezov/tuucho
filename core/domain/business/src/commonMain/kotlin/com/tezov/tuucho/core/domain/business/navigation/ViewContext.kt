package com.tezov.tuucho.core.domain.business.navigation

import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.StateViewProtocol

data class ViewContext(
    val url: String,
    val view: Lazy<ViewProtocol>,
    val state: StateViewProtocol,
//    val animation: AnimationProtocol = 'default'
)