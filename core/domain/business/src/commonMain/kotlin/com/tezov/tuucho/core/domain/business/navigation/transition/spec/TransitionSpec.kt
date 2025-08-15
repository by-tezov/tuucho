package com.tezov.tuucho.core.domain.business.navigation.transition.spec

sealed class TransitionSpec {

    object None : TransitionSpec()

    class Fade(
        val duration_ms: Int = TransitionFadeSpec.duration_ms,
    ) : TransitionSpec()

    class SlideHorizontal(
        val duration_ms: Int = TransitionSlideSpec.Horizontal.duration_ms,
        val outDarkAlphaFactor: Float = TransitionSlideSpec.Horizontal.outDarkAlphaFactor,
        val entrance: TransitionSlideSpec.Horizontal.Entrance = TransitionSlideSpec.Horizontal.Entrance.FromEnd,
        val effect: TransitionSlideSpec.Effect = TransitionSlideSpec.Effect.CoverPush,
    ) : TransitionSpec()

    class SlideVertical(
        val duration_ms: Int = TransitionSlideSpec.Vertical.duration_ms,
        val outDarkAlphaFactor: Float = TransitionSlideSpec.Vertical.outDarkAlphaFactor,
        val entrance: TransitionSlideSpec.Vertical.Entrance = TransitionSlideSpec.Vertical.Entrance.FromBottom,
        val effect: TransitionSlideSpec.Effect = TransitionSlideSpec.Effect.CoverPush,
    ) : TransitionSpec()
}