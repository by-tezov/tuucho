package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser._system.JsonElementTree
import com.tezov.tuucho.core.data.parser.breaker._system.JsonEntityObjectTreeProducerProtocol
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.MaterialSchema
import kotlinx.serialization.json.JsonObject
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
        val version: String,
        val rootJsonEntity: JsonElementTree?,
        val jsonElementTree: MutableList<JsonElementTree>,
    )

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        materialObject: JsonObject,
        jsonEntityObjectTreeProducer: JsonEntityObjectTreeProducerProtocol,
    ): Parts = with(materialObject.withScope(MaterialSchema::Scope)) {
        val jsonElementTree: MutableList<JsonElementTree> = mutableListOf()

        components?.let {
            componentBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
        }?.also(jsonElementTree::add)

        contents?.let {
            contentBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
        }?.also(jsonElementTree::add)
        styles?.let {
            styleBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
        }?.also(jsonElementTree::add)
        options?.let {
            optionBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
        }?.also(jsonElementTree::add)

        texts?.let {
            textBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
        }?.also(jsonElementTree::add)
        colors?.let {
            colorBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
        }?.also(jsonElementTree::add)
        dimensions?.let {
            dimensionBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
        }?.also(jsonElementTree::add)

        Parts(
            version = version
                ?: throw DataException.Default("missing version in page material $this"),
            rootJsonEntity = rootComponent?.let { component ->
                jsonEntityObjectTreeProducer.invoke(component)
            },
            jsonElementTree = jsonElementTree
        )
    }
}