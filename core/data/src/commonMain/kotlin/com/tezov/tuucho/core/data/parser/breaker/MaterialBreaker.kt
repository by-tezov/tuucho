package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.parser._system.JsonElementTree
import com.tezov.tuucho.core.data.parser.breaker._system.JsonObjectEntityTreeFactoryProtocol
import com.tezov.tuucho.core.data.parser.breaker._system.VersioningEntityFactoryProtocol
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
        val versionEntity: VersioningEntity,
        val rootJsonObjectEntity: JsonObjectEntity?,
        val jsonElementTree: List<JsonElementTree>,
    )

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        materialObject: JsonObject,
        versioningEntityFactory: VersioningEntityFactoryProtocol,
        jsonObjectEntityTreeProducer: JsonObjectEntityTreeFactoryProtocol,
    ): Parts = with(materialObject.withScope(MaterialSchema::Scope)) {
        val jsonElementTree = buildList {
            components?.let {
                componentBreaker.process("".toPath(), it, jsonObjectEntityTreeProducer)
            }?.also(::add)

            contents?.let {
                contentBreaker.process("".toPath(), it, jsonObjectEntityTreeProducer)
            }?.also(::add)
            styles?.let {
                styleBreaker.process("".toPath(), it, jsonObjectEntityTreeProducer)
            }?.also(::add)
            options?.let {
                optionBreaker.process("".toPath(), it, jsonObjectEntityTreeProducer)
            }?.also(::add)

            texts?.let {
                textBreaker.process("".toPath(), it, jsonObjectEntityTreeProducer)
            }?.also(::add)
            colors?.let {
                colorBreaker.process("".toPath(), it, jsonObjectEntityTreeProducer)
            }?.also(::add)
            dimensions?.let {
                dimensionBreaker.process("".toPath(), it, jsonObjectEntityTreeProducer)
            }?.also(::add)
        }
        Parts(
            versionEntity = versioningEntityFactory.invoke(pageSetting),
            rootJsonObjectEntity = rootComponent?.let { component ->
                jsonObjectEntityTreeProducer.invoke(component)
            }?.content,
            jsonElementTree = jsonElementTree
        )
    }
}