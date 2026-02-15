@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.assembler.material._system

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

// TODO: Not efficient at all
internal class JsonObjectMerger {
    fun merge(
        from: List<JsonObject>
    ) = if (from.size == 1) {
        from.first()
    } else {
        JsonNull
            .withScope(::SchemaScope)
            .apply { from.asReversed().forEach { merge(it, true) } }
            .collect()
    }

    private fun SchemaScope.merge(
        next: JsonObject,
        isRoot: Boolean
    ) {
        for ((key, nextChild) in next) {
            val previousChild = this[key]
            this[key] = if (previousChild is JsonObject && nextChild is JsonObject) {
                previousChild
                    .withScope(::SchemaScope)
                    .apply {
                        when {
                            key == IdSchema.root -> mergeId(nextChild, isRoot)
                            else -> merge(nextChild, false)
                        }
                    }.collect()
            } else {
                nextChild
            }
        }
    }

    private fun SchemaScope.mergeId(
        next: JsonObject,
        isRoot: Boolean
    ) {
        if (isRoot) {
            next
                .withScope(IdSchema::Scope)
                .apply {
                    remove(IdSchema.Key.source) // we remove source, since previous is the source about to be merged
                }.collect()
                .let { merge(it, true) }
        } else {
            merge(next, false)
        }
    }
}
