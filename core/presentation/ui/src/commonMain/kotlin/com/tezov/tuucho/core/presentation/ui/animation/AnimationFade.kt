package com.tezov.tuucho.core.presentation.ui.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.AnimationDirection
import com.tezov.tuucho.core.domain.business.navigation.transitionOption.setting.AnimationSetting
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.ui.animation._system.ModifierAnimation

object AnimationFade {

    fun AnimationProgress.fade(
        config: AnimationSetting.Fade,
        directionContent: AnimationDirection.Content
    ): ModifierAnimation {
        return when (directionContent) {
            AnimationDirection.Content.Enter -> In(config, this)
            AnimationDirection.Content.Exit -> Out(config, this)
        }
    }

    class In(
        private val config: AnimationSetting.Fade,
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
        private val config: AnimationSetting.Fade,
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