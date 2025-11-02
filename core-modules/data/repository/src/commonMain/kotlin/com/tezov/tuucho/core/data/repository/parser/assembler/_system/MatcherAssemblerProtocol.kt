@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.assembler._system

import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

@TuuchoExperimentalAPI
interface MatcherAssemblerProtocol {
    fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean
}
