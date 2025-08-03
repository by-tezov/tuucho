package com.tezov.tuucho.core.data.parser.assembler._element.layout.linear


import com.tezov.tuucho.core.data.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import com.tezov.tuucho.core.data.parser.rectifier._element.layout.linear.ContentLayoutLinearItemsMatcher as ContentLayoutLinearItemsMatcherRectifier

class ContentLayoutLinearItemsMatcher : MatcherAssemblerProtocol {

    private val matcher = ContentLayoutLinearItemsMatcherRectifier()

    override fun accept(path: JsonElementPath, element: JsonElement) = matcher.accept(path, element)

}