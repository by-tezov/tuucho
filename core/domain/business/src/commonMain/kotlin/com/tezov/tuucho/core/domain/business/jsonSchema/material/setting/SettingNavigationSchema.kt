package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object SettingNavigationSchema {

    const val root = "navigation"

    object Key {
        const val selector = Selector.root
        const val option = Option.root
        const val transition = Transition.root
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = SettingSchema.root
        var self by delegate<JsonObject?>(root)

        var selector by delegate<JsonObject?>(Key.selector)
        var option by delegate<JsonObject?>(Key.option)
        var transition by delegate<JsonObject?>(Key.transition)
    }

    object Selector {
        const val root = "selector"

        object Key {
            const val type = "type"
            const val value = "value"
            const val values = "values"
        }

        object Value {
            object Type {
                const val pageBreadCrumb = "page-bread-crumb"
            }
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = Selector.root
            var self by delegate<JsonObject?>(root)

            var type by delegate<String?>(Key.type)
            var values by delegate<JsonArray?>(Key.values)
        }
    }

    object Option {

        const val root = "option"

        object Key {
            const val single = "single"
            const val reuse = "reuse"
            const val clearStack = "clear-stack"
            const val popupTo = PopUpTo.root
        }

        object Value {
            object Reuse {
                const val first = "first"
                const val last = "last"
            }
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = Option.root
            var self by delegate<JsonObject?>(root)

            var single by delegate<Boolean?>(Key.single)
            var reuse by delegate<String?>(Key.reuse)
            var clearStack by delegate<Boolean?>(Key.clearStack)
            var popupTo by delegate<JsonObject?>(Key.popupTo)
        }

        object PopUpTo {
            const val root = "pop-up-to"

            object Key {
                const val url = "url"
                const val inclusive = "inclusive"
                const val greedy = "greedy"
            }

            class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
                override val root = PopUpTo.root

                var url by delegate<String?>(Key.url)
                var inclusive by delegate<Boolean?>(Key.inclusive)
                var greedy by delegate<Boolean?>(Key.greedy)
            }
        }

    }

    object Transition {

        const val root = "transition"

        object Key {
            const val enter = "enter"
            const val exit = "exit"
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = Transition.root
            var self by delegate<JsonObject?>(root)

            var enter by delegate<JsonObject?>(Key.enter)
            var exit by delegate<JsonObject?>(Key.exit)
        }

        object Set {

            object Key {
                const val push = "push"
                const val pop = "pop"
            }

            class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {

                var push by delegate<JsonObject?>(Key.push)
                var pop by delegate<JsonObject?>(Key.pop)
            }

        }

        object Spec {

            object Key {
                const val type = "type"
            }

            object Value {
                object Type {
                    const val fade = "fade"
                    const val slideHorizontal = "slide-horizontal"
                    const val slideVertical = "slide-vertical"
                }
            }

            class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

            open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) : OpenSchemaScope<T>(argument) {

                var type by delegate<String?>(Key.type)
            }

        }

    }

}