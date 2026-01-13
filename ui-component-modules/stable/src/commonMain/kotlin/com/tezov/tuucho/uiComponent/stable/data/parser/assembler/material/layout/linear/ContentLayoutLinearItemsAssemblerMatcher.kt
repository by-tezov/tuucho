@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.uiComponent.stable.data.parser.assembler.material.layout.linear

import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerMatcherProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.layout.linear.ContentLayoutLinearItemsRectifierMatcher
import kotlinx.serialization.json.JsonElement

class ContentLayoutLinearItemsAssemblerMatcher : AssemblerMatcherProtocol {
    private val matcher = ContentLayoutLinearItemsRectifierMatcher()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)
}
