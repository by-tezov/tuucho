package com.tezov.tuucho.core.domain.business.navigation.transitionOption.setting

object AnimationSlideSetting {

    enum class Effect { CoverPush, Cover, Push }

    object Horizontal {

        internal const val duration_ms = 200
        internal const val outDarkAlphaFactor = 0.75f

        enum class Entrance { FromEnd, FromStart }
    }

    object Vertical {

        internal const val duration_ms = 300
        internal const val outDarkAlphaFactor = 0.60f

        enum class Entrance { FromBottom, FromTop }
    }
}