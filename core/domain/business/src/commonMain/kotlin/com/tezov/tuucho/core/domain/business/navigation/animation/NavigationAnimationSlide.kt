package com.tezov.tuucho.core.domain.business.navigation.animation

object NavigationAnimationSlide {

    enum class Effect { CoverPush, Cover, Push }

    object Horizontal {

        internal const val DURATION_ms = 200
        internal const val OUT_DARK_ALPHA_FACTOR = 0.75f

        enum class Entrance { FromEnd, FromStart }


    }

    object Vertical {

        internal const val DURATION_ms = 300
        internal const val OUT_DARK_ALPHA_FACTOR = 0.60f

        enum class Entrance { FromBottom, FromTop }


    }
}