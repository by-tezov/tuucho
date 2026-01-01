package com.tezov.tuucho.core.presentation.ui.view.protocol

import kotlinx.serialization.json.JsonObject

interface ViewFactoryMatcherProtocol {
    fun accept(
        componentObject: JsonObject
    ): Boolean
}
