@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.assembler._element.layout.linear

import com.tezov.tuucho.core.data.repository.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import com.tezov.tuucho.core.data.repository.parser.rectifier._element.layout.linear.ContentLayoutLinearItemsMatcher as ContentLayoutLinearItemsMatcherRectifier

@OptIn(TuuchoExperimentalAPI::class)
class ContentLayoutLinearItemsMatcher : MatcherAssemblerProtocol {
    private val matcher = ContentLayoutLinearItemsMatcherRectifier()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)
}
