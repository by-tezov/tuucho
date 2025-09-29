package com.tezov.tuucho.core.domain.tool.json

fun String.toPath() = JsonElementPath(this)

class JsonElementPath(path: String? = null) : Iterable<String> {

    private val _path: String? = path.trim()

    private companion object {
        const val SEPARATOR = "/"
    }

    private fun String?.trim() = this
        ?.replace(Regex("/+"), SEPARATOR)
        ?.removePrefix(SEPARATOR)
        ?.takeIf { it.isNotEmpty() }

    fun isEmpty() = _path?.isEmpty() ?: true

    fun lastSegment() = _path?.substringAfterLast(SEPARATOR)

    fun child(segment: String): JsonElementPath {
        val _segment = segment.trim()
        return if (_segment.isNullOrEmpty()) this
        else JsonElementPath(if (_path.isNullOrEmpty()) _segment else "$_path$SEPARATOR$_segment")
    }

    fun parent(): JsonElementPath {
        if (_path.isNullOrEmpty()) return this
        return JsonElementPath(
            if (_path.contains(SEPARATOR)) {
                _path.substringBeforeLast(SEPARATOR)
            } else {
                ""
            }
        )
    }

    override fun iterator() = _path?.split(SEPARATOR)?.iterator() ?: emptyList<String>().iterator()

    override fun toString() = _path ?: ""
}
