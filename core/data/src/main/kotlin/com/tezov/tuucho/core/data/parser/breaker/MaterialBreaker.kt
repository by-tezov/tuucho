package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.parser._system.JsonEntityElement
import com.tezov.tuucho.core.domain._system.toPath
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialBreaker : KoinComponent {

    private val colorBreaker: ColorBreaker by inject()
    private val dimensionBreaker: DimensionBreaker by inject()
    private val textBreaker: TextBreaker by inject()

    private val styleBreaker: StyleBreaker by inject()
    private val contentBreaker: ContentBreaker by inject()
    private val componentBreaker: ComponentBreaker by inject()

    data class Parts(
        var rootJsonEntity: JsonEntityElement? = null,
        val jsonEntityElement: MutableList<JsonEntityElement> = mutableListOf()
    )

    fun process(
        material: JsonObject,
        extraData: ExtraDataBreaker,
    ) = Parts().apply {
        val mutableMap = material.toMutableMap()

        mutableMap[com.tezov.tuucho.core.domain.schema.MaterialSchema.Key.colors]?.let {
            colorBreaker.process("".toPath(), it, extraData)
        }?.also(jsonEntityElement::add)

        mutableMap[com.tezov.tuucho.core.domain.schema.MaterialSchema.Key.dimensions]?.let {
            dimensionBreaker.process("".toPath(), it, extraData)
        }?.also(jsonEntityElement::add)

        mutableMap[com.tezov.tuucho.core.domain.schema.MaterialSchema.Key.texts]?.let {
            textBreaker.process("".toPath(), it, extraData)
        }?.also(jsonEntityElement::add)

        mutableMap[com.tezov.tuucho.core.domain.schema.MaterialSchema.Key.styles]?.let {
            styleBreaker.process("".toPath(), it, extraData)
        }?.also(jsonEntityElement::add)

        mutableMap[com.tezov.tuucho.core.domain.schema.MaterialSchema.Key.contents]?.let {
            contentBreaker.process("".toPath(), it, extraData)
        }?.also(jsonEntityElement::add)

        mutableMap[com.tezov.tuucho.core.domain.schema.MaterialSchema.Key.components]?.let {
            componentBreaker.process("".toPath(), it, extraData)
        }?.also(jsonEntityElement::add)

        mutableMap[com.tezov.tuucho.core.domain.schema.MaterialSchema.Key.root]?.let { component ->
            componentBreaker.process("".toPath(), component, extraData)
        }?.also { rootJsonEntity = it }

    }

}