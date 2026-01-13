package com.tezov.tuucho.core.domain.tool.json

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath.Companion.INDEX_SEPARATOR

fun String.toPath(
    index: Int? = null
) = JsonElementPath(this, index)

fun Int.toIndexPath() = "${INDEX_SEPARATOR}$this"

val ROOT_PATH = JsonElementPath()

class JsonElementPath(
    path: String? = null,
    index: Int? = null,
) : Iterable<String> {
    private val _path: String? = path.trim(index)

    companion object {
        const val SEGMENT_SEPARATOR = "/"
        const val INDEX_SEPARATOR = "#"
    }

    private fun String?.trim(
        index: Int? = null
    ): String? {
        val base = this
            ?.replace(Regex("/+"), SEGMENT_SEPARATOR)
            ?.removePrefix(SEGMENT_SEPARATOR)
            ?.removeSuffix(SEGMENT_SEPARATOR)
            ?.takeIf { it.isNotEmpty() }
        return when {
            index == null -> base
            base.isNullOrEmpty() -> "$INDEX_SEPARATOR$index"
            else -> "$base$INDEX_SEPARATOR$index"
        }
    }

    fun isEmpty() = _path?.isEmpty() ?: true

    fun lastSegment() = _path?.substringAfterLast(SEGMENT_SEPARATOR)

    fun child(
        segment: String,
        index: Int? = null,
    ) = JsonElementPath(if (_path.isNullOrEmpty()) segment else "$_path$SEGMENT_SEPARATOR$segment", index)

    fun atIndex(
        index: Int
    ) = JsonElementPath(_path, index)

    fun parent(): JsonElementPath {
        if (_path.isNullOrEmpty()) return this
        val last = _path.substringAfterLast(SEGMENT_SEPARATOR)
        return when {
            last.contains(INDEX_SEPARATOR) -> {
                JsonElementPath(_path.substringBeforeLast(INDEX_SEPARATOR))
            }

            _path.contains(SEGMENT_SEPARATOR) -> {
                JsonElementPath(_path.substringBeforeLast(SEGMENT_SEPARATOR))
            }

            else -> {
                ROOT_PATH
            }
        }
    }

    override fun iterator() = Regex("""#\d+|[^/#]+""")
        .findAll(_path.orEmpty())
        .map { it.value }
        .iterator()

    override fun toString() = _path ?: ""
}
