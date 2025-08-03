package com.tezov.tuucho.core.data.parser.shadower._element.layout.linear

import com.tezov.tuucho.core.data.parser.shadower._system.MatcherShadowerProtocol
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import kotlinx.serialization.json.JsonElement
import com.tezov.tuucho.core.data.parser.rectifier._element.layout.linear.ContentLayoutLinearItemsMatcher as ContentLayoutLinearItemsMatcherRectifier

class ContentLayoutLinearItemsMatcher : MatcherShadowerProtocol {

    private val matcher = ContentLayoutLinearItemsMatcherRectifier()

    override fun accept(path: JsonElementPath, element: JsonElement) = matcher.accept(path, element)

}