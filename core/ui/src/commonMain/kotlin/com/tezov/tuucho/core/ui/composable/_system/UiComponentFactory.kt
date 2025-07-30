package com.tezov.tuucho.core.ui.composable._system

import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

abstract class UiComponentFactory : MatcherUiComponentProtocol, KoinComponent {

    abstract fun process(componentElement: JsonObject): ComposableScreenProtocol

}

abstract class Screen : ComposableScreenProtocol {

    fun interface Processor {
        operator fun invoke(jsonObject: JsonObject): Unit
    }

    private val processors = mutableMapOf<String, Processor>()

    private fun keyOf(type: String?, id: String?) = "$type+$id"

    final override fun update(jsonObject: JsonObject) {
        val id = jsonObject.onScope(IdSchema::Scope).value
        val type = jsonObject.withScope(TypeSchema::Scope).self
        processors[keyOf(type, id)]?.invoke(jsonObject)
    }

    protected fun addProcessor(type: String?, id: String?, processor: Processor) {
        val key = keyOf(type, id)
        if (processors.contains(key)) {
            println("Warning, processor for key $key already exist")
        }
        processors.put(key, processor)
    }



}