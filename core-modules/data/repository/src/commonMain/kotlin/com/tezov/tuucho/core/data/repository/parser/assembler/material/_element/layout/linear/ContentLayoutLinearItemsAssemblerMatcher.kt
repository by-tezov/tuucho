@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.assembler.material._element.layout.linear

import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerMatcherProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.layout.linear.ContentLayoutLinearItemsRectifierMatcher as ContentLayoutLinearItemsMatcherRectifier

class ContentLayoutLinearItemsAssemblerMatcher : AssemblerMatcherProtocol {
    private val matcher = ContentLayoutLinearItemsMatcherRectifier()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)
}
