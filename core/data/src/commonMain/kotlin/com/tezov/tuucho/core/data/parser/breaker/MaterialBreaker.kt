package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.parser._system.JsonElementTree
import com.tezov.tuucho.core.data.parser.breaker._system.ArgumentBreaker
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema.material.MaterialSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialBreaker : KoinComponent {

    private val componentBreaker: ComponentBreaker by inject()
    private val contentBreaker: ContentBreaker by inject()
    private val styleBreaker: StyleBreaker by inject()
    private val optionBreaker: OptionBreaker by inject()

    private val colorBreaker: ColorBreaker by inject()
    private val dimensionBreaker: DimensionBreaker by inject()
    private val textBreaker: TextBreaker by inject()

    data class Parts(
        var rootJsonEntity: JsonElementTree? = null,
        val jsonElementTree: MutableList<JsonElementTree> = mutableListOf(),
    )

    fun process(
        element: JsonElement,
        argument: ArgumentBreaker,
    ) = Parts().apply {
        val mutableMap = element.jsonObject.toMutableMap()
        mutableMap[MaterialSchema.Key.root]?.let { component ->
            componentBreaker.process("".toPath(), component, argument)
        }?.also { rootJsonEntity = it }
        mutableMap[MaterialSchema.Key.components]?.let {
            componentBreaker.process("".toPath(), it, argument)
        }?.also(jsonElementTree::add)
        mutableMap[MaterialSchema.Key.contents]?.let {
            contentBreaker.process("".toPath(), it, argument)
        }?.also(jsonElementTree::add)
        mutableMap[MaterialSchema.Key.styles]?.let {
            styleBreaker.process("".toPath(), it, argument)
        }?.also(jsonElementTree::add)
        mutableMap[MaterialSchema.Key.options]?.let {
            optionBreaker.process("".toPath(), it, argument)
        }?.also(jsonElementTree::add)

        mutableMap[MaterialSchema.Key.texts]?.let {
            textBreaker.process("".toPath(), it, argument)
        }?.also(jsonElementTree::add)
        mutableMap[MaterialSchema.Key.colors]?.let {
            colorBreaker.process("".toPath(), it, argument)
        }?.also(jsonElementTree::add)
        mutableMap[MaterialSchema.Key.dimensions]?.let {
            dimensionBreaker.process("".toPath(), it, argument)
        }?.also(jsonElementTree::add)
    }

}