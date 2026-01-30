package com.tezov.tuucho.core.data.repository.parser.breaker

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business._system.koin.Associate.getAllAssociated
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@OpenForTest
internal class MaterialBreaker : TuuchoKoinComponent {
    data class Nodes(
        val rootJsonObject: JsonObject?,
        val jsonObjects: List<JsonObject>,
    )

    sealed class Association {
        object Breakable : Association()
    }

    private val breakables: List<String> by lazy {
        getKoin().getAllAssociated(Association.Breakable::class)
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        materialObject: JsonObject,
    ) = with(materialObject.withScope(MaterialSchema::Scope)) {
        Nodes(
            rootJsonObject = rootComponent?.let(::JsonObject),
            jsonObjects = buildList {
                breakables.forEach { breakable ->
                    this@with[breakable]?.process()?.also(::addAll)
                }
            }
        )
    }

    private fun JsonElement.process(): List<JsonObject> {
        if (this !is JsonArray) {
            throw DataException.Default("By design it must be an array because object should has been rectified but $this")
        }
        return map { entry ->
            if (entry !is JsonObject) {
                throw DataException.Default("By design it must be an object but $entry")
            }
            entry
        }
    }
}
