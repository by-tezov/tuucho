package com.tezov.tuucho.core.data.repository.parser.shadower

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.shadower._system.ShadowerMatcherProtocol
import com.tezov.tuucho.core.data.repository.parser.shadower._system.ShadowerProtocol
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

abstract class AbstractShadower :
    ShadowerProtocol,
    TuuchoKoinComponent {
    protected open val matchers: List<ShadowerMatcherProtocol> = emptyList()
    protected open val childProcessors: List<ShadowerProtocol> = emptyList()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = matchers.any { it.accept(path, element) }

    override suspend fun FlowCollector<JsonObject>.process(
        path: JsonElementPath,
        element: JsonElement
    ) {
        with(element.find(path)) {
            when (this) {
                is JsonArray -> processArray(this@process)
                is JsonObject -> processObject(path, element, this@process)
                is JsonPrimitive -> throw DataException.Default("By design can't assemble primitive")
            }
        }
    }

    private suspend fun JsonArray.processArray(
        flowCollector: FlowCollector<JsonObject>
    ) {
        forEach { entry ->
            (entry as? JsonObject)
                ?: throw DataException.Default(
                    "By design element inside array must be object, so there is surely something missing in the rectifier for $entry "
                )
            entry.processObject(ROOT_PATH, entry, flowCollector)
        }
    }

    private suspend fun JsonObject.processObject(
        path: JsonElementPath,
        element: JsonElement,
        flowCollector: FlowCollector<JsonObject>
    ) {
        if (childProcessors.isNotEmpty()) {
            keys.forEach { childKey ->
                val childPath = path.child(childKey)
                childProcessors
                    .filter { it.accept(path = childPath, element = element) }
                    .forEach {
                        it.run {
                            with(flowCollector) { process(path = childPath, element = element) }
                        }
                    }
            }
        }
        flowCollector.emit(this)
    }

}
