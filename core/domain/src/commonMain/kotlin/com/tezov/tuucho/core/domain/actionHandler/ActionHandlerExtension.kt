package com.tezov.tuucho.core.domain.actionHandler

private val COMMAND_SEPARATOR = Regex.escape("://")
private val AUTHORITY_SEPARATOR = Regex.escape("/")

fun String.command(): String {
    val regex = Regex("^(.+?)$COMMAND_SEPARATOR")
    return regex.find(this)?.groupValues?.get(1)
        ?: throw IllegalStateException("missing action in string")
}

fun String.authority(): String {
    val pattern = "^.+?$COMMAND_SEPARATOR(.+?)$AUTHORITY_SEPARATOR"
    val regex = Regex(pattern)
    return regex.find(this)?.groupValues?.get(1)
        ?: throw IllegalStateException("missing authority in action")
}

fun String.target(): String {
    val pattern = "^.+?$COMMAND_SEPARATOR.+?$AUTHORITY_SEPARATOR(.*)"
    return pattern.toRegex() .find(this)?.groupValues?.get(1) ?:
        throw IllegalStateException("missing target in action")
}