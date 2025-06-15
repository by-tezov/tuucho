package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.parser._schema.MaterialSchema
import com.tezov.tuucho.core.data.parser._system.JsonEntityElement
import com.tezov.tuucho.core.data.parser._system.toPath
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialBreaker : KoinComponent {

    private val colorBreaker: ColorBreaker by inject()
    private val dimensionBreaker: DimensionBreaker by inject()
    private val textBreaker: TextBreaker by inject()

    private val optionBreaker: OptionBreaker by inject()
    private val styleBreaker: StyleBreaker by inject()
    private val contentBreaker: ContentBreaker by inject()
    private val componentBreaker: ComponentBreaker by inject()

    data class Parts(
        var rootJsonEntity: JsonEntityElement? = null,
        val jsonEntityElement: MutableList<JsonEntityElement> = mutableListOf()
    )

    fun encode(
        material: JsonObject,
        config: ExtraDataBreaker,
    ) = Parts().apply {
        val mutableMap = material.toMutableMap()

        mutableMap[MaterialSchema.Name.colors]?.let {
            colorBreaker.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchema.Name.dimensions]?.let {
            dimensionBreaker.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchema.Name.texts]?.let {
            textBreaker.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchema.Name.options]?.let {
            optionBreaker.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchema.Name.styles]?.let {
            styleBreaker.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchema.Name.contents]?.let {
            contentBreaker.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchema.Name.components]?.let {
            componentBreaker.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchema.Name.root]?.let { component ->
            componentBreaker.process("".toPath(), component, config)
        }?.also { rootJsonEntity = it }
    }

}