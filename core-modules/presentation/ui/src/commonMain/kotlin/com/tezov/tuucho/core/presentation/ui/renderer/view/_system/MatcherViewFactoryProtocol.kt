@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.renderer.view._system

import kotlinx.serialization.json.JsonObject

interface MatcherViewFactoryProtocol {
    fun accept(
        componentElement: JsonObject
    ): Boolean
}
