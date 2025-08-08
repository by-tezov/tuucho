package com.tezov.tuucho.core.domain.business.navigation.animation

sealed class NavigationAnimationType {
    object None : NavigationAnimationType()

    class Fade(
        val duration_ms: Int = NavigationAnimationFade.DURATION_ms,
    ) : NavigationAnimationType()

    class SlideHorizontal(
        val duration_ms: Int = NavigationAnimationSlide.Horizontal.DURATION_ms,
        val outDarkAlphaFactor: Float = NavigationAnimationSlide.Horizontal.OUT_DARK_ALPHA_FACTOR,
        val entrance: NavigationAnimationSlide.Horizontal.Entrance = NavigationAnimationSlide.Horizontal.Entrance.FromEnd,
        val effect: NavigationAnimationSlide.Effect = NavigationAnimationSlide.Effect.CoverPush,
    ) : NavigationAnimationType()

    class SlideVertical(
        val duration_ms: Int = NavigationAnimationSlide.Vertical.DURATION_ms,
        val outDarkAlphaFactor: Float = NavigationAnimationSlide.Vertical.OUT_DARK_ALPHA_FACTOR,
        val entrance: NavigationAnimationSlide.Vertical.Entrance = NavigationAnimationSlide.Vertical.Entrance.FromBottom,
        val effect: NavigationAnimationSlide.Effect = NavigationAnimationSlide.Effect.CoverPush,
    ) : NavigationAnimationType()
}