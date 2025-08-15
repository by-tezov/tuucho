package com.tezov.tuucho.core.presentation.ui.transition

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
import com.tezov.tuucho.core.domain.business.navigation.transition.TransitionDirection
import com.tezov.tuucho.core.domain.business.navigation.transition.spec.TransitionSlideSpec
import com.tezov.tuucho.core.domain.business.navigation.transition.spec.TransitionSpec
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.ui.transition._system.ModifierAnimation

object TransitionSlideHorizontal {

    fun AnimationProgress.slideHorizontal(
        spec: TransitionSpec.SlideHorizontal,
        directionNav: TransitionDirection.Navigation,
        directionScreen: TransitionDirection.Screen,
    ): ModifierAnimation {
        return when (directionScreen) {
            TransitionDirection.Screen.Enter -> when (directionNav) {
                TransitionDirection.Navigation.Push -> In(
                    config = spec,
                    animationProgress = this,
                    directionNav = TransitionDirection.Navigation.Push
                )

                TransitionDirection.Navigation.Pop -> Out(
                    config = spec,
                    animationProgress = this,
                    directionNav = TransitionDirection.Navigation.Pop
                )
            }

            TransitionDirection.Screen.Exit -> when (directionNav) {
                TransitionDirection.Navigation.Push -> Out(
                    config = spec,
                    animationProgress = this,
                    directionNav = TransitionDirection.Navigation.Push
                )

                TransitionDirection.Navigation.Pop -> In(
                    config = spec,
                    animationProgress = this,
                    directionNav = TransitionDirection.Navigation.Pop
                )
            }
        }
    }

    class In(
        private val config: TransitionSpec.SlideHorizontal,
        private val animationProgress: AnimationProgress,
        directionNav: TransitionDirection.Navigation,
    ) : ModifierAnimation() {

        private val startValue = when (directionNav) {
            TransitionDirection.Navigation.Push -> 1.0f
            TransitionDirection.Navigation.Pop -> 0.0f
        }
        private val endValue = when (directionNav) {
            TransitionDirection.Navigation.Push -> 0.0f
            TransitionDirection.Navigation.Pop -> 1.0f
        }

        private val entranceFactor = when (config.entrance) {
            TransitionSlideSpec.Horizontal.Entrance.FromEnd -> 1.0f
            TransitionSlideSpec.Horizontal.Entrance.FromStart -> -1.0f
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
//            val width = LocalActivity.size.width * entranceFactor //TODO
            val width = 1000.dp
            return offset(x = width * progress.value, y = 0.dp)
        }
    }

    class Out(
        private val config: TransitionSpec.SlideHorizontal,
        private val animationProgress: AnimationProgress,
        directionNav: TransitionDirection.Navigation,
    ) : ModifierAnimation() {

        private val startValue = when (directionNav) {
            TransitionDirection.Navigation.Push -> 0.0f
            TransitionDirection.Navigation.Pop -> -1.0f
        }
        private val endValue = when (directionNav) {
            TransitionDirection.Navigation.Push -> -1.0f
            TransitionDirection.Navigation.Pop -> 0.0f
        }

        private val entranceFactor = when (config.entrance) {
            TransitionSlideSpec.Horizontal.Entrance.FromEnd -> 1.0f
            TransitionSlideSpec.Horizontal.Entrance.FromStart -> -1.0f
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

            val width = when (config.effect) {
                TransitionSlideSpec.Effect.CoverPush -> {
//                    (LocalActivity.size.width * entranceFactor) / 2 //TODO
                      150.dp
                }

                TransitionSlideSpec.Effect.Push -> {
//                    (LocalActivity.size.width * entranceFactor) //TODO
                    150.dp
                }

                TransitionSlideSpec.Effect.Cover -> {
                    0.dp
                }
            }
            return offset(x = width * progress.value, y = 0.dp)
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