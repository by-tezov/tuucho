package com.tezov.tuucho.core.domain.business.model

import com.tezov.tuucho.core.domain.business.exception.DomainException

class ActionModelDomain private constructor(
    val command: String,
    val authority: String?,
    val target: String?,
) {

    companion object {
        private val COMMAND_SEPARATOR = Regex.escape("://")
        private val AUTHORITY_SEPARATOR = Regex.escape("/")

        private fun String.command(): String {
            val regex = Regex("^(.+?)$COMMAND_SEPARATOR")
            return regex.find(this)?.groupValues?.get(1)
                ?: throw DomainException.Default("missing action in string")
        }

        private fun String.authority(): String? {
            val pattern = "^.+?$COMMAND_SEPARATOR(.+?)(?:$AUTHORITY_SEPARATOR|$)"
            val regex = Regex(pattern)
            return regex.find(this)?.groupValues?.get(1)
        }

        private fun String.target(): String? {
            val pattern = "^.+?$COMMAND_SEPARATOR.+?$AUTHORITY_SEPARATOR(.*)"
            return pattern.toRegex().find(this)?.groupValues?.get(1)
        }

        fun from(value: String) = ActionModelDomain(
            command = value.command(),
            authority = value.authority(),
            target = value.target(),
        )

        fun from(command: String, authority: String?, target: String?) = ActionModelDomain(
            command = command,
            authority = authority,
            target = target,
        )
    }

}
