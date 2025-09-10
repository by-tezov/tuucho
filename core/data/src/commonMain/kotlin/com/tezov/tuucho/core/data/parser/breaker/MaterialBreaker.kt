package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.parser._system.JsonElementNode
import com.tezov.tuucho.core.data.parser._system.JsonObjectNode
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface MaterialBreakerProtocol : KoinComponent {

    data class Nodes(
        val rootJsonObjectNode: JsonObjectNode?,
        val jsonElementNodes: List<JsonElementNode>,
    )

    suspend fun process(
        materialObject: JsonObject,
    ): Nodes
}

class MaterialBreaker : MaterialBreakerProtocol {

    private val componentBreaker: ComponentBreaker by inject()
    private val contentBreaker: ContentBreaker by inject()
    private val styleBreaker: StyleBreaker by inject()
    private val optionBreaker: OptionBreaker by inject()

    private val colorBreaker: ColorBreaker by inject()
    private val dimensionBreaker: DimensionBreaker by inject()
    private val textBreaker: TextBreaker by inject()

    override suspend fun process(
        materialObject: JsonObject,
    ) = with(materialObject.withScope(MaterialSchema::Scope)) {
        MaterialBreakerProtocol.Nodes(
            rootJsonObjectNode = rootComponent?.let(::JsonObjectNode),
            jsonElementNodes = buildList {
                components?.let {
                    componentBreaker.process("".toPath(), it)
                }?.also(::add)

                contents?.let {
                    contentBreaker.process("".toPath(), it)
                }?.also(::add)
                styles?.let {
                    styleBreaker.process("".toPath(), it)
                }?.also(::add)
                options?.let {
                    optionBreaker.process("".toPath(), it)
                }?.also(::add)

                texts?.let {
                    textBreaker.process("".toPath(), it)
                }?.also(::add)
                colors?.let {
                    colorBreaker.process("".toPath(), it)
                }?.also(::add)
                dimensions?.let {
                    dimensionBreaker.process("".toPath(), it)
                }?.also(::add)
            }
        )
    }

}