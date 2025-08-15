package com.tezov.tuucho.core.presentation.ui.transition

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.tezov.tuucho.core.domain.business.navigation.transition.TransitionDirection
import com.tezov.tuucho.core.domain.business.navigation.transition.spec.TransitionSpec
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.ui.transition._system.ModifierAnimation

object TransitionFade {

    fun AnimationProgress.fade(
        spec: TransitionSpec.Fade,
        directionScreen: TransitionDirection.Screen
    ): ModifierAnimation {
        return when (directionScreen) {
            TransitionDirection.Screen.Enter -> In(spec, this)
            TransitionDirection.Screen.Exit -> Out(spec, this)
        }
    }

    class In(
        private val config: TransitionSpec.Fade,
        private val animationProgress: AnimationProgress,
    ) : ModifierAnimation() {

        @Composable
        override fun Modifier.animate(): Modifier {
            val progress = animationProgress.animateFloat(
                startValue = 0.0f,
                endValue = 1.0f,
                animationSpecToEnd = tween(
                    durationMillis = config.duration_ms,
                    easing = LinearEasing
                )
            )
            return alpha(progress.value)
        }
    }

    class Out(
        private val config: TransitionSpec.Fade,
        private val animationProgress: AnimationProgress,
    ) : ModifierAnimation() {

        @Composable
        override fun Modifier.animate(): Modifier {
            val progress = animationProgress.animateFloat(
                startValue = 1.0f,
                endValue = 0.5f,
                animationSpecToEnd = tween(
                    durationMillis = config.duration_ms,
                    easing = LinearEasing
                )
            )
            return alpha(progress.value)
        }
    }

}