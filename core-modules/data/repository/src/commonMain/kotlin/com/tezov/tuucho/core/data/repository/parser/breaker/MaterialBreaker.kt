package com.tezov.tuucho.core.data.repository.parser.breaker

import com.tezov.tuucho.core.data.repository.parser._system.JsonElementNode
import com.tezov.tuucho.core.data.repository.parser._system.JsonObjectNode
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

@OpenForTest
class MaterialBreaker : TuuchoKoinComponent {
    data class Nodes(
        val rootJsonObjectNode: JsonObjectNode?,
        val jsonElementNodes: List<JsonElementNode>,
    )

    private val componentBreaker: ComponentBreaker by inject()
    private val contentBreaker: ContentBreaker by inject()
    private val styleBreaker: StyleBreaker by inject()
    private val optionBreaker: OptionBreaker by inject()

    private val colorBreaker: ColorBreaker by inject()
    private val dimensionBreaker: DimensionBreaker by inject()
    private val textBreaker: TextBreaker by inject()
    private val actionBreaker: ActionBreaker by inject()

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        materialObject: JsonObject,
    ) = with(materialObject.withScope(MaterialSchema::Scope)) {
        Nodes(
            rootJsonObjectNode = rootComponent?.let(::JsonObjectNode),
            jsonElementNodes = buildList {
                components
                    ?.let {
                        componentBreaker.process("".toPath(), it)
                    }?.also(::add)

                contents
                    ?.let {
                        contentBreaker.process("".toPath(), it)
                    }?.also(::add)
                styles
                    ?.let {
                        styleBreaker.process("".toPath(), it)
                    }?.also(::add)
                options
                    ?.let {
                        optionBreaker.process("".toPath(), it)
                    }?.also(::add)

                texts
                    ?.let {
                        textBreaker.process("".toPath(), it)
                    }?.also(::add)
                colors
                    ?.let {
                        colorBreaker.process("".toPath(), it)
                    }?.also(::add)
                dimensions
                    ?.let {
                        dimensionBreaker.process("".toPath(), it)
                    }?.also(::add)
                actions
                    ?.let {
                        actionBreaker.process("".toPath(), it)
                    }?.also(::add)
            }
        )
    }
}
