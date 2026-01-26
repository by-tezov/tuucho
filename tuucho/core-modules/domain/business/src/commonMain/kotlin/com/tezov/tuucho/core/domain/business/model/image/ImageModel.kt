package com.tezov.tuucho.core.domain.business.model.image

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.tool.extension.ExtensionNull.isNotNullAndNotEmpty
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

data class ImageModel(
    val command: String,
    val target: String,
    val query: JsonElement?,
    val tag: String?,
) {
    companion object {
        private val COMMAND_SEPARATOR = Regex.escape("://")
        private val QUERY_SEPARATOR = Regex.escape("?")

        @Suppress("ktlint:standard:max-line-length")
        private val IMAGE_REGEX = Regex(
            pattern = """^([^$COMMAND_SEPARATOR]+)$COMMAND_SEPARATOR([^$QUERY_SEPARATOR]+)(?:$QUERY_SEPARATOR(.+))?$"""
        )

        fun String.toJsonElement(): JsonElement? {
            if (isEmpty()) return null

            return when {
                contains("=") -> {
                    val pairs = split("&")
                        .map {
                            val parts = it.split("=", limit = 2)
                            parts[0] to JsonPrimitive(parts[1])
                        }
                    JsonObject(pairs.toMap())
                }

                contains(",") -> {
                    val items = split(",").mapNotNull { item ->
                        item
                            .takeIf { it.isNotNullAndNotEmpty() }
                            ?.let(::JsonPrimitive)
                    }
                    JsonArray(items)
                }

                else -> {
                    JsonPrimitive(this)
                }
            }
        }

        fun from(
            value: String,
            tag: String?
        ): ImageModel {
            val match = IMAGE_REGEX.matchEntire(value)
                ?: throw DomainException.Default("invalid image")
            return ImageModel(
                command = match.groups[1]?.value
                    ?: throw DomainException.Default("image command can't be null"),
                target = match.groups[2]?.value
                    ?: throw DomainException.Default("image target can't be null"),
                query = match.groups[3]?.value?.toJsonElement(),
                tag = tag
            )
        }

        fun from(
            command: String,
            target: String,
            query: String? = null,
            tag: String?
        ) = ImageModel(
            command = command,
            target = target,
            query = query?.toJsonElement(),
            tag = tag,
        )
    }

    override fun toString(): String {
        fun JsonElement.toPrettyString(): String = when (this) {
            is JsonObject -> entries.joinToString("&") { "${it.key}=${it.value.string}" }
            is JsonArray -> joinToString(",") { it.string }
            is JsonPrimitive -> this.content
        }

        val stringBuilder = StringBuilder().apply {
            append(command)
            append("://")
            append(target)
            query?.let { query ->
                append("?")
                append(query.toPrettyString())
            }
        }
        return stringBuilder.toString()
    }
}
