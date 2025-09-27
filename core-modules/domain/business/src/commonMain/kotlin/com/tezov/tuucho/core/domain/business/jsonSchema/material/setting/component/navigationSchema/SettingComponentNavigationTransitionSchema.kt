package com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.DirectionNavigation
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.DirectionScreen
import kotlinx.serialization.json.JsonObject

object SettingComponentNavigationTransitionSchema {

    const val root = "transition"

    object Key {
        const val forward = DirectionNavigation.forward
        const val backward = DirectionNavigation.backward
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = SettingComponentNavigationTransitionSchema.root

        var forward by delegate<JsonObject?>(Key.forward)
        var backward by delegate<JsonObject?>(Key.backward)
    }

    object Set {

        object Key {
            const val enter = DirectionScreen.enter
            const val exit = DirectionScreen.exit
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {

            var enter by delegate<JsonObject?>(Key.enter)
            var exit by delegate<JsonObject?>(Key.exit)
        }

    }

    object Spec {

        object Key {
            const val type = "type"
            const val directionNavigation = "direction-navigation"
            const val directionScreen = "direction-screen"
        }

        object Value {
            object Type {
                const val none = "none"
                const val fade = "fade"
                const val slideHorizontal = "slide-horizontal"
                const val slideVertical = "slide-vertical"
            }

            object DirectionNavigation {
                const val forward = "forward"
                const val backward = "backward"
            }

            object DirectionScreen {
                const val enter = "enter"
                const val exit = "exit"
            }
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

        open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
            OpenSchemaScope<T>(argument) {
            var type by delegate<String?>(Key.type)
            var directionNavigation by delegate<String?>(Key.directionNavigation)
            var directionScreen by delegate<String?>(Key.directionScreen)
        }

    }

    object SpecFade {

        object Key {
            const val duration = "duration"
            const val alphaInitial = "alpha-initial"
        }

        class Scope(argument: SchemaScopeArgument) : Spec.OpenScope<Scope>(argument) {

            var duration by delegate<String?>(Key.duration)
            var alphaInitial by delegate<String?>(Key.alphaInitial)
        }
    }

    object SpecSlide {

        object Key {
            const val duration = "duration"
            const val exitDarkAlphaFactor = "exit-dark-alpha-factor"
            const val entrance = "entrance"
            const val effect = "effect"
        }

        object Value {
            object Entrance {
                const val fromEnd = "from-end"
                const val fromStart = "from-start"
                const val fromTop = "from-top"
                const val fromBottom = "from-bottom"
            }

            object Effect {
                const val coverPush = "cover-push"
                const val cover = "cover"
                const val push = "push"
            }
        }

        class Scope(argument: SchemaScopeArgument) : Spec.OpenScope<Scope>(argument) {
            var duration by delegate<String?>(Key.duration)
            var exitDarkAlphaFactor by delegate<String?>(Key.exitDarkAlphaFactor)
            var entrance by delegate<String?>(Key.entrance)
            var effect by delegate<String?>(Key.effect)
        }
    }

}