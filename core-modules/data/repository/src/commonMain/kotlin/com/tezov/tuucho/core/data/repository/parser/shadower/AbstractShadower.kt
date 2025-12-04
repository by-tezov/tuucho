package com.tezov.tuucho.core.data.repository.parser.shadower

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.data.repository.parser.shadower._system.MatcherShadowerProtocol
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

abstract class AbstractShadower :
    MatcherShadowerProtocol,
    TuuchoKoinComponent {
    protected open val matchers: List<MatcherShadowerProtocol> = emptyList()
    protected open val childProcessors: List<AbstractShadower> = emptyList()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = matchers.any { it.accept(path, element) }

    suspend fun process(
        path: JsonElementPath,
        element: JsonElement,
        jsonObjectConsumer: JsonObjectConsumerProtocol,
    ) {
        with(element.find(path)) {
            when (this) {
                is JsonArray -> processArray(jsonObjectConsumer)
                is JsonObject -> processObject(path, element, jsonObjectConsumer)
                is JsonPrimitive -> throw DataException.Default("By design can't assemble primitive")
            }
        }
    }

    private suspend fun JsonArray.processArray(
        jsonObjectConsumer: JsonObjectConsumerProtocol,
    ) {
        forEach { entry ->
            (entry as? JsonObject)
                ?: throw DataException.Default(
                    "By design element inside array must be object, so there is surely something missing in the rectifier for $entry "
                )
            entry.processObject(ROOT_PATH, entry, jsonObjectConsumer)
        }
    }

    private suspend fun JsonObject.processObject(
        path: JsonElementPath,
        element: JsonElement,
        jsonObjectConsumer: JsonObjectConsumerProtocol,
    ) {
        if (childProcessors.isNotEmpty()) {
            keys.forEach { childKey ->
                val childPath = path.child(childKey)
                childProcessors
                    .filter { it.accept(childPath, element) }
                    .forEach { it.process(childPath, element, jsonObjectConsumer) }
            }
        }
        jsonObjectConsumer.onNext(this, null)
    }
}
