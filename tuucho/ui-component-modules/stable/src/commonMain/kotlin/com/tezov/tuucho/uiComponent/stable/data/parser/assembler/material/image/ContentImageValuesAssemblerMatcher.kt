@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.uiComponent.stable.data.parser.assembler.material.image

import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerMatcherProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.image.content.ContentImageValuesRectifierMatcher
import kotlinx.serialization.json.JsonElement

class ContentImageValuesAssemblerMatcher : AssemblerMatcherProtocol {
    private val matcher = ContentImageValuesRectifierMatcher()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)
}
