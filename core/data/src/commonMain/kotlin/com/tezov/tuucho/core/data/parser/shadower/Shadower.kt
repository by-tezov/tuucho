package com.tezov.tuucho.core.data.parser.shadower

import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.data.parser.shadower._system.MatcherShadowerProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.KoinComponent

abstract class Shadower : MatcherShadowerProtocol, KoinComponent {
    protected open val matchers: List<MatcherShadowerProtocol> = emptyList()
    protected open val childProcessors: List<Shadower> = emptyList()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = matchers.any { it.accept(path, element) }

    fun process(
        path: JsonElementPath,
        element: JsonElement,
        jsonObjectConsumer: JsonObjectConsumerProtocol,
    ) {
        with(element.find(path)) {
            when (this) {
                is JsonArray -> processArray(jsonObjectConsumer)
                is JsonObject -> processObject(path, element, jsonObjectConsumer)
                is JsonPrimitive -> throw DataException.Default("by design can't assemble primitive")
            }
        }
    }

    private fun JsonArray.processArray(
        jsonObjectConsumer: JsonObjectConsumerProtocol,
    ) {
        forEach { entry ->
            (entry as? JsonObject)
                ?: throw DataException.Default("By design element inside array must be object, so there is surely something missing in the rectifier for $entry ")
            entry.processObject("".toPath(), entry, jsonObjectConsumer)
        }
    }

    private fun JsonObject.processObject(
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
        jsonObjectConsumer.invoke(this)
    }
}