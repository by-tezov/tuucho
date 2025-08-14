package com.tezov.tuucho.core.presentation.ui.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.AnimationDirection
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.setting.AnimationSetting
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.setting.AnimationSlideSetting
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.ui.animation._system.ModifierAnimation

object AnimationSlideVertical {

    fun AnimationProgress.slideVertical(
        config: AnimationSetting.SlideVertical,
        directionNav: AnimationDirection.Navigation,
        directionContent: AnimationDirection.Content,
    ): ModifierAnimation {
        return when (directionContent) {
            AnimationDirection.Content.Enter -> when (directionNav) {
                AnimationDirection.Navigation.Push -> In(
                    config = config,
                    animationProgress = this,
                    directionNav = AnimationDirection.Navigation.Push
                )

                AnimationDirection.Navigation.Pop -> Out(
                    config = config,
                    animationProgress = this,
                    directionNav = AnimationDirection.Navigation.Pop
                )
            }

            AnimationDirection.Content.Exit -> when (directionNav) {
                AnimationDirection.Navigation.Push -> Out(
                    config = config,
                    animationProgress = this,
                    directionNav = AnimationDirection.Navigation.Push
                )

                AnimationDirection.Navigation.Pop -> In(
                    config = config,
                    animationProgress = this,
                    directionNav = AnimationDirection.Navigation.Pop
                )
            }
        }
    }

    class In(
        private val config: AnimationSetting.SlideVertical,
        private val animationProgress: AnimationProgress,
        directionNav: AnimationDirection.Navigation,
    ) : ModifierAnimation() {

        private val startValue = when (directionNav) {
            AnimationDirection.Navigation.Push -> 1.0f
            AnimationDirection.Navigation.Pop -> 0.0f
        }
        private val endValue = when (directionNav) {
            AnimationDirection.Navigation.Push -> 0.0f
            AnimationDirection.Navigation.Pop -> 1.0f
        }

        private val entranceFactor = when (config.entrance) {
            AnimationSlideSetting.Vertical.Entrance.FromBottom -> 1.0f
            AnimationSlideSetting.Vertical.Entrance.FromTop -> -1.0f
        }

        @Composable
        override fun Modifier.animate(): Modifier {
            val progress = animationProgress.animateFloat(
                startValue = startValue,
                endValue = endValue,
                animationSpecToEnd = tween(
                    durationMillis = config.duration_ms,
                    easing = LinearEasing
                )
            )
//            val height = LocalActivity.size.height * entranceFactor //TODO
            val height = 1000.dp
            return offset(x = 0.dp, y = height * progress.value)
        }
    }

    class Out(
        private val config: AnimationSetting.SlideVertical,
        private val animationProgress: AnimationProgress,
        directionNav: AnimationDirection.Navigation,
    ) : ModifierAnimation() {

        private val startValue = when (directionNav) {
            AnimationDirection.Navigation.Push -> 0.0f
            AnimationDirection.Navigation.Pop -> -1.0f
        }
        private val endValue = when (directionNav) {
            AnimationDirection.Navigation.Push -> -1.0f
            AnimationDirection.Navigation.Pop -> 0.0f
        }

        private val entranceFactor = when (config.entrance) {
            AnimationSlideSetting.Vertical.Entrance.FromBottom -> 1.0f
            AnimationSlideSetting.Vertical.Entrance.FromTop -> -1.0f
        }

        @Composable
        override fun Modifier.animate(): Modifier {
            val progress = animationProgress.animateFloat(
                startValue = startValue,
                endValue = endValue,
                animationSpecToEnd = tween(
                    durationMillis = config.duration_ms,
                    easing = LinearEasing
                )
            )
            val height = when (config.effect) {
                AnimationSlideSetting.Effect.CoverPush -> {
//                    (LocalActivity.size.height * entranceFactor) / 4 //TODO
                    150.dp
                }

                AnimationSlideSetting.Effect.Push -> {
//                    (LocalActivity.size.height * entranceFactor) //TODO
                    150.dp
                }

                AnimationSlideSetting.Effect.Cover -> {
                    0.dp
                }
            }
            return this
                .offset(x = 0.dp, y = height * progress.value)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = Color.Black,
                        alpha = -progress.value * config.outDarkAlphaFactor,
                        topLeft = Offset(0f, 0f),
                        size = size,
                        style = Fill
                    )
                }
        }
    }

}