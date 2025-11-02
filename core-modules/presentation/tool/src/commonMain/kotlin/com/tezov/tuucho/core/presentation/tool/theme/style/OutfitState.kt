package com.tezov.tuucho.core.presentation.tool.theme.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.tezov.tuucho.core.presentation.tool.delegate.DelegateNullFallBack
import kotlin.reflect.KClass

typealias OutfitStateNull<T> = OutfitState.Null.Style<T>
typealias OutfitStateSimple<T> = OutfitState.Simple.Style<T>
typealias OutfitStateBiStable<T> = OutfitState.BiStable.Style<T>
typealias OutfitStateInput<T> = OutfitState.Input.Style<T>
typealias OutfitStateSemantic<T> = OutfitState.Semantic.Style<T>
typealias OutfitStateTemplate<T> = OutfitState.Template.Style<T>

object OutfitState {
    interface Style<T : Any> {
        fun selectorType(): KClass<*>

        fun selectorDefault(): Any

        fun resolve(
            selector: Any? = null
        ): T?

        fun isSelectorValid(
            selector: Any? = null
        ) = selector != null && selector::class == selectorType()

        @Suppress("UNCHECKED_CAST")
        fun <S : Any> resolve(
            selector: Any? = null,
            doResolve: ((selector: S) -> T?)
        ): T? = runCatching {
            (selector.takeIf { isSelectorValid(it) } ?: selectorDefault()) as S
        }.getOrNull()?.let(doResolve)

        @Suppress("UNCHECKED_CAST")
        fun <C : Any> resolve(
            selector: Any? = null,
            type: KClass<C>
        ): C? = resolve(selector)?.let { runCatching { it as C }.getOrNull() }
    }

    object Null {
        class Style<T : Any> : OutfitState.Style<T> {
            companion object {
                @Composable
                fun <T : Any> Style<T>.copy() = this
            }

            override fun selectorType() = Unit::class

            override fun selectorDefault() = Unit

            override fun resolve(
                selector: Any?
            ) = null
        }
    }

    object Simple {
        object Selector

        class StyleBuilder<T : Any> internal constructor(
            val style: Style<T>
        ) {
            var value = style.value

            internal fun get() = Style(
                value = value,
            )
        }

        class Style<T : Any>(
            val value: T,
        ) : OutfitState.Style<T> {
            companion object {
                @Composable
                fun <T : Any> Style<T>.copy(
                    builder: @Composable StyleBuilder<T>.() -> Unit = {}
                ) = StyleBuilder(this)
                    .also {
                        it.builder()
                    }.get()

                inline val Color.asStateSimple: OutfitStateSimple<Color>
                    get() = OutfitStateSimple(
                        value = this
                    )
                inline val TextStyle.asStateSimple: OutfitStateSimple<TextStyle>
                    get() = OutfitStateSimple(
                        value = this
                    )
            }

            constructor(style: Style<T>) : this(
                value = style.value,
            )

            override fun selectorType() = Selector::class

            override fun selectorDefault() = Selector

            override fun resolve(
                selector: Any?
            ) = resolve<Selector>(selector) {
                value
            }
        }
    }

    object BiStable {
        enum class Selector {
            Active,
            Inactive
        }

        class StyleBuilder<T : Any> internal constructor(
            val style: Style<T>
        ) {
            var active = style.active
            var inactive = style.inactive

            internal fun get() = Style(
                active = active,
                inactive = inactive,
            )
        }

        class Style<T : Any>(
            active: T? = null,
            inactive: T? = null,
        ) : OutfitState.Style<T> {
            private val delegates = DelegateNullFallBack.Group<T>()
            val active: T by delegates.ref(active)
            val inactive: T by delegates.ref(inactive)

            init {
                delegates.fallBackValue = delegates.firstNotNull()?.fallBackValue
            }

            companion object {
                @Composable
                fun <T : Any> Style<T>.copy(
                    builder: @Composable StyleBuilder<T>.() -> Unit = {}
                ) = StyleBuilder(this)
                    .also {
                        it.builder()
                    }.get()

                inline val Color.asStateBiStable: OutfitStateBiStable<Color>
                    get() = OutfitStateBiStable(
                        active = this
                    )
                inline val TextStyle.asStateBiStable: OutfitStateBiStable<TextStyle>
                    get() = OutfitStateBiStable(
                        active = this
                    )
            }

            constructor(style: Style<T>) : this(
                active = style.active,
                inactive = style.inactive,
            )

            override fun selectorType() = Selector::class

            override fun selectorDefault() = Selector.Active

            override fun resolve(
                selector: Any?
            ) = resolve<Selector>(selector) {
                when (it) {
                    Selector.Active -> active
                    Selector.Inactive -> inactive
                }
            }
        }
    }

    object Input {
        enum class Selector {
            Inactive,
            Error,
            Focused,
            Unfocused
        }

        class StyleBuilder<T : Any> internal constructor(
            val style: Style<T>
        ) {
            var inactive = style.inactive
            var error = style.error
            var focused = style.focused
            var unfocused = style.unfocused

            internal fun get() = Style(
                inactive = inactive,
                error = error,
                focused = focused,
                unfocused = unfocused,
            )
        }

        class Style<T : Any>(
            inactive: T? = null,
            error: T? = null,
            focused: T? = null,
            unfocused: T? = null,
        ) : OutfitState.Style<T> {
            private val delegates = DelegateNullFallBack.Group<T>()
            val inactive: T by delegates.ref(inactive)
            val error: T by delegates.ref(error)
            val focused: T by delegates.ref(focused)
            val unfocused: T by delegates.ref(unfocused)

            init {
                delegates.fallBackValue = delegates.firstNotNull()?.fallBackValue
            }

            companion object {
                @Composable
                fun <T : Any> Style<T>.copy(
                    builder: @Composable StyleBuilder<T>.() -> Unit = {}
                ) = StyleBuilder(this)
                    .also {
                        it.builder()
                    }.get()

                inline val Color.asStateInput: OutfitStateInput<Color>
                    get() = OutfitStateInput(
                        unfocused = this
                    )
                inline val TextStyle.asStateInput: OutfitStateInput<TextStyle>
                    get() = OutfitStateInput(
                        unfocused = this
                    )
            }

            constructor(style: Style<T>) : this(
                inactive = style.inactive,
                error = style.error,
                focused = style.focused,
                unfocused = style.unfocused,
            )

            override fun selectorType() = Selector::class

            override fun selectorDefault() = Selector.Unfocused

            override fun resolve(
                selector: Any?
            ) = resolve<Selector>(selector) {
                when (it) {
                    Selector.Inactive -> inactive
                    Selector.Error -> error
                    Selector.Focused -> focused
                    Selector.Unfocused -> unfocused
                }
            }
        }
    }

    object Semantic {
        enum class Selector {
            Neutral,
            Info,
            Alert,
            Error,
            Success
        }

        class StyleBuilder<T : Any> internal constructor(
            val style: Style<T>
        ) {
            var neutral = style.neutral
            var info = style.info
            var alert = style.alert
            var error = style.error
            var success = style.success

            internal fun get() = Style(
                neutral = neutral,
                info = info,
                alert = alert,
                error = error,
                success = success,
            )
        }

        class Style<T : Any>(
            neutral: T? = null,
            info: T? = null,
            alert: T? = null,
            success: T? = null,
            error: T? = null,
        ) : OutfitState.Style<T> {
            private val delegates = DelegateNullFallBack.Group<T>()
            val neutral: T by delegates.ref(neutral)
            val info: T by delegates.ref(info)
            val alert: T by delegates.ref(alert)
            val error: T by delegates.ref(error)
            val success: T by delegates.ref(success)

            init {
                delegates.fallBackValue = delegates.firstNotNull()?.fallBackValue
            }

            companion object {
                @Composable
                fun <T : Any> Style<T>.copy(
                    builder: @Composable StyleBuilder<T>.() -> Unit = {}
                ) = StyleBuilder(this)
                    .also {
                        it.builder()
                    }.get()

                inline val Color.asStateSemantic: OutfitStateSemantic<Color>
                    get() = OutfitStateSemantic(
                        neutral = this
                    )
                inline val TextStyle.asStateSemantic: OutfitStateSemantic<TextStyle>
                    get() = OutfitStateSemantic(
                        neutral = this
                    )
            }

            constructor(style: Style<T>) : this(
                neutral = style.neutral,
                info = style.info,
                alert = style.alert,
                error = style.error,
                success = style.success,
            )

            override fun selectorType() = Selector::class

            override fun selectorDefault() = Selector.Neutral

            override fun resolve(
                selector: Any?
            ) = resolve<Selector>(selector) {
                when (it) {
                    Selector.Neutral -> neutral
                    Selector.Info -> info
                    Selector.Alert -> alert
                    Selector.Error -> error
                    Selector.Success -> success
                }
            }
        }
    }

    object Template {
        enum class Selector {
            A,
            B,
            C,
            D,
            E,
            F,
            G
        }

        class StyleBuilder<T : Any> internal constructor(
            val style: Style<T>
        ) {
            var a = style.a
            var b = style.b
            var c = style.c
            var d = style.d
            var e = style.e
            var f = style.f
            var g = style.g

            internal fun get() = Style(
                a = a,
                b = b,
                c = c,
                d = d,
                e = e,
                f = f,
                g = g,
            )
        }

        class Style<T : Any>(
            a: T? = null,
            b: T? = null,
            c: T? = null,
            d: T? = null,
            e: T? = null,
            f: T? = null,
            g: T? = null,
        ) : OutfitState.Style<T> {
            private val delegates = DelegateNullFallBack.Group<T>()
            val a: T by delegates.ref(a)
            val b: T by delegates.ref(b)
            val c: T by delegates.ref(c)
            val d: T by delegates.ref(d)
            val e: T by delegates.ref(e)
            val f: T by delegates.ref(f)
            val g: T by delegates.ref(g)

            init {
                delegates.fallBackValue = delegates.firstNotNull()?.fallBackValue
            }

            companion object {
                @Composable
                fun <T : Any> Style<T>.copy(
                    builder: @Composable StyleBuilder<T>.() -> Unit = {}
                ) = StyleBuilder(this)
                    .also {
                        it.builder()
                    }.get()

                inline val Color.asStateTemplate: OutfitStateTemplate<Color>
                    get() = OutfitStateTemplate(
                        a = this
                    )
                inline val TextStyle.asStateTemplate: OutfitStateTemplate<TextStyle>
                    get() = OutfitStateTemplate(a = this)
            }

            constructor(style: Style<T>) : this(
                a = style.a,
                b = style.b,
                c = style.c,
                d = style.d,
                e = style.e,
                f = style.f,
                g = style.g,
            )

            override fun selectorType() = Selector::class

            override fun selectorDefault() = Selector.A

            override fun resolve(
                selector: Any?
            ) = resolve<Selector>(selector) {
                when (it) {
                    Selector.A -> a
                    Selector.B -> b
                    Selector.C -> c
                    Selector.D -> d
                    Selector.E -> e
                    Selector.F -> f
                    Selector.G -> g
                }
            }
        }
    }
}
