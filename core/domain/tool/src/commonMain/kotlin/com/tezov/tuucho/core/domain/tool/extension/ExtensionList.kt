package com.tezov.tuucho.core.domain.tool.extension

object ExtensionList {

    const val NULL_INDEX = -1

    val Int.isNullIndex get() = this == NULL_INDEX

    val Int.isNotNullIndex get() = this != NULL_INDEX

    fun <T> List<T>.priorLast(): T {
        if (size < 2) throw NoSuchElementException("List is doesn't have prior last.")
        return this[size - 2]
    }

    fun <T> List<T>.priorLastOrNull() =
        if (size < 2) null else this[size - 2]

    fun <T> List<T>.subListToEnd(fromIndex: Int) = subList(fromIndex, this.size)

    fun <T> ArrayDeque<T>.push(t: T) = this.addLast(t)

    fun <T> ArrayDeque<T>.pop() = this.removeLast()

    fun <T> ArrayDeque<T>.popOrNull() = this.removeLastOrNull()

}