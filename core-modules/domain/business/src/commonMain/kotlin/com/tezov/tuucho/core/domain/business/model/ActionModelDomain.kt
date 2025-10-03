package com.tezov.tuucho.core.domain.business.model

import com.tezov.tuucho.core.domain.business.exception.DomainException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

data class ActionModelDomain(
    val command: String,
    val authority: String?,
    val target: String?,
    val query: JsonElement?,
) {

    companion object {
        private val COMMAND_SEPARATOR = Regex.escape("://")
        private val AUTHORITY_SEPARATOR = Regex.escape("/")
        private val QUERY_SEPARATOR = Regex.escape("?")

        private val ACTION_REGEX = Regex(
            pattern = """^([^$COMMAND_SEPARATOR]+)$COMMAND_SEPARATOR(?:([^$AUTHORITY_SEPARATOR$QUERY_SEPARATOR]+)(?:$AUTHORITY_SEPARATOR([^$QUERY_SEPARATOR]+))?)?(?:$QUERY_SEPARATOR(.+))?$"""
        )

        private fun String.toQueryToMap(): JsonElement? {
            if (isEmpty()) return null

            return when {
                contains("=") -> {
                    val pairs = split("&")
                        .mapNotNull {
                            val parts = it.split("=", limit = 2)
                            if (parts.size == 2) parts[0] to JsonPrimitive(parts[1]) else null
                        }
                    JsonObject(pairs.toMap())
                }

                contains(",") -> {
                    val items = split(",").map { JsonPrimitive(it) }
                    JsonArray(items)
                }

                else -> JsonPrimitive(this)
            }
        }

        fun from(value: String): ActionModelDomain {
            val match = ACTION_REGEX.matchEntire(value)
                ?: throw DomainException.Default("invalid action")
            val (command: String, authority: String?, target: String?, query: String?) = match.destructured
            return ActionModelDomain(
                command = command,
                authority = authority,
                target = target,
                query = query?.toQueryToMap(),
            )
        }

        fun from(
            command: String,
            authority: String?,
            target: String?,
            query: String? = null,
        ) = from(command, authority, target, query?.toQueryToMap())

        fun from(
            command: String,
            authority: String?,
            target: String?,
            query: JsonElement?,
        ) = ActionModelDomain(
            command = command,
            authority = authority,
            target = target,
            query = query,
        )
    }

}