package com.tezov.tuucho.core.domain.business.navigation.transitionOption.setting

sealed class AnimationSetting {

    object None : AnimationSetting()

    class Fade(
        val duration_ms: Int = AnimationFadeSetting.duration_ms,
    ) : AnimationSetting()

    class SlideHorizontal(
        val duration_ms: Int = AnimationSlideSetting.Horizontal.duration_ms,
        val outDarkAlphaFactor: Float = AnimationSlideSetting.Horizontal.outDarkAlphaFactor,
        val entrance: AnimationSlideSetting.Horizontal.Entrance = AnimationSlideSetting.Horizontal.Entrance.FromEnd,
        val effect: AnimationSlideSetting.Effect = AnimationSlideSetting.Effect.CoverPush,
    ) : AnimationSetting()

    class SlideVertical(
        val duration_ms: Int = AnimationSlideSetting.Vertical.duration_ms,
        val outDarkAlphaFactor: Float = AnimationSlideSetting.Vertical.outDarkAlphaFactor,
        val entrance: AnimationSlideSetting.Vertical.Entrance = AnimationSlideSetting.Vertical.Entrance.FromBottom,
        val effect: AnimationSlideSetting.Effect = AnimationSlideSetting.Effect.CoverPush,
    ) : AnimationSetting()
}