package com.tezov.tuucho.core.data.parser.encoder

import com.tezov.tuucho.core.data.parser._schema.MaterialSchemaData
import com.tezov.tuucho.core.data.parser._system.JsonEntityElement
import com.tezov.tuucho.core.data.parser._system.flatten
import com.tezov.tuucho.core.data.parser._system.toPath
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialSchemaDataEncoder : KoinComponent {

    private val optionEncoder: OptionSchemaDataEncoder by inject()
    private val styleEncoder: StyleSchemaDataEncoder by inject()
    private val contentEncoder: ContentSchemaDataEncoder by inject()
    private val componentEncoder: ComponentSchemaDataEncoder by inject()

    data class Parts(
        var rootJsonEntity: JsonEntityElement? = null,
        val jsonEntityElement: MutableList<JsonEntityElement> = mutableListOf()
    )

    fun encode(
        material: JsonObject,
        config: EncoderConfig,
    ) = Parts().apply {
        val mutableMap = material.toMutableMap()

        //colors, dimensions, texts

        println("+++++++++++++++++++++++++++++++++++++++++++")
        println(material)

        mutableMap[MaterialSchemaData.Name.options]?.let {
            optionEncoder.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchemaData.Name.styles]?.let {
            styleEncoder.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchemaData.Name.contents]?.let {
            contentEncoder.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchemaData.Name.components]?.let {
            componentEncoder.process("".toPath(), it, config)
        }?.also(jsonEntityElement::add)

        mutableMap[MaterialSchemaData.Name.root]?.let { component ->
            componentEncoder.process("".toPath(), component, config)
        }?.also { rootJsonEntity = it }

        rootJsonEntity?.flatten()
            ?.flatMap { it.flatten() }
            ?.map { it.content }
            ?.forEach { println(it) }

        jsonEntityElement
            .flatMap { it.flatten() }
            .map { it.content }
            .forEach { println(it) }
    }

}