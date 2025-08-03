package com.tezov.tuucho.core.domain.tool.extension

object ExtensionIterable {

    fun <T> Iterator<T>.nextOrNull() = if (hasNext()) next() else null

    inline fun <T> Iterable<T>.forEach(offset: Int = 0, action: (T) -> Unit) =
        drop(offset).forEach(action)

    inline fun <T> Iterable<T>.forEachIndexed(offset: Int = 0, action: (index: Int, T) -> Unit) {
        var index = offset
        drop(offset).forEach { item ->
            action(index, item)
            index++
        }
    }

    inline fun <T> Iterable<T>.find(offset: Int = 0, predicate: (T) -> Boolean): T? =
        drop(offset).find(predicate)

    inline fun <T> Iterable<T>.findIndexed(
        offset: Int = 0,
        predicate: (index: Int, T) -> Boolean,
    ): T? {
        forEachIndexed(offset) { index, element ->
            if (predicate(index, element)) return element
        }
        return null
    }

    inline fun <T> Iterable<T>.findIndex(offset: Int = 0, predicate: (T) -> Boolean): Int? {
        forEachIndexed(offset) { index, element ->
            if (predicate(element)) return index
        }
        return null
    }

    inline fun <T> MutableIterable<T>.remove(offset: Int = 0, predicate: (T) -> Boolean): T? {
        val iterator: MutableIterator<T> = this.iterator()
        for (i in 0 until offset) {
            iterator.next()
        }
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (predicate(next)) {
                iterator.remove()
                return next
            }
        }
        return null
    }

}