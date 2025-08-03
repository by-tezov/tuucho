package com.tezov.tuucho.core.presentation.tool.delegate

import kotlin.reflect.KProperty

object DelegateNullFallBack {

    class Ref<V : Any>(initialValue: V? = null, fallBackValue: (() -> V)? = null) {

        var value: (() -> V)? = null
            private set

        var fallBackValue: (() -> V)?
            get() = value
            set(fallBackValue) {
                value ?: fallBackValue?.let {
                    value = it
                }
            }

        init {
            initialValue?.let {
                value = { it }
            } ?: run {
                this.fallBackValue = fallBackValue
            }
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): V {
            return value!!.invoke()
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
            this.value = { value }
        }

    }

    class Group<V : Any> {

        private val refs = mutableListOf<Ref<V>>()

        fun ref(initialValue: V?, fallBackValue: (() -> V)? = null) =
            Ref(initialValue, fallBackValue).also { refs.add(it) }

        var fallBackValue: (() -> V)?
            get() = refs.firstOrNull()?.fallBackValue
            set(fallBackValue) {
                fallBackValue?.let {
                    refs.forEach { delegate ->
                        delegate.fallBackValue = fallBackValue
                    }
                }
            }

        fun firstNotNull() = refs.find { it.value != null }
    }

}