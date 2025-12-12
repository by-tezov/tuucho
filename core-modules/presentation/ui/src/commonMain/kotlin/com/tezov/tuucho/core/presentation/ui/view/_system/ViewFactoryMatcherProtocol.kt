@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.view._system

import kotlinx.serialization.json.JsonObject

interface ViewFactoryMatcherProtocol {
    fun accept(
        componentObject: JsonObject
    ): Boolean
}
