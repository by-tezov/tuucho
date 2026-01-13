@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.uiComponent.stable.data.parser.shadower.layout.linear

import com.tezov.tuucho.core.data.repository.parser.shadower._system.ShadowerMatcherProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.layout.linear.ContentLayoutLinearItemsRectifierMatcher
import kotlinx.serialization.json.JsonElement

class ContentLayoutLinearItemsMatcher : ShadowerMatcherProtocol {
    private val matcher = ContentLayoutLinearItemsRectifierMatcher()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)
}
