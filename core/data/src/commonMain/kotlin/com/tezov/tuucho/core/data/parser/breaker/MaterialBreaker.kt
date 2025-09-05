package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.parser._system.JsonElementTree
import com.tezov.tuucho.core.data.parser.breaker._system.JsonEntityObjectTreeProducerProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema
import com.tezov.tuucho.core.domain.tool.json.toPath
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
        val rootJsonEntity: JsonElementTree?,
        val jsonElementTree: List<JsonElementTree>,
    )

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        materialObject: JsonObject,
        jsonEntityObjectTreeProducer: JsonEntityObjectTreeProducerProtocol,
    ): Parts = with(materialObject.withScope(MaterialSchema::Scope)) {
        val jsonElementTree = buildList {
            components?.let {
                componentBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
            }?.also(::add)

            contents?.let {
                contentBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
            }?.also(::add)
            styles?.let {
                styleBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
            }?.also(::add)
            options?.let {
                optionBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
            }?.also(::add)

            texts?.let {
                textBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
            }?.also(::add)
            colors?.let {
                colorBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
            }?.also(::add)
            dimensions?.let {
                dimensionBreaker.process("".toPath(), it, jsonEntityObjectTreeProducer)
            }?.also(::add)
        }
        Parts(
            rootJsonEntity = rootComponent?.let { component ->
                jsonEntityObjectTreeProducer.invoke(component)
            },
            jsonElementTree = jsonElementTree
        )
    }
}