package com.tezov.tuucho.core.presentation.ui.transition

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.ui.transition._system.DirectionScreen
import com.tezov.tuucho.core.presentation.ui.transition._system.ModifierTransition
import kotlinx.serialization.json.JsonObject

object TransitionFade {

    class Spec(
        val duration: Int,
        val alphaInitial: Float,
    ) {
        companion object {
            fun from(specObject: JsonObject?) = specObject
                ?.withScope(SettingNavigationTransitionSchema.SpecFade::Scope)
                .let {
                    Spec(
                        duration = it?.duration?.toIntOrNull() ?: 250,
                        alphaInitial = it?.alphaInitial?.toFloatOrNull() ?: 0.1f,
                    )
                }
        }
    }

    @Composable
    fun AnimationProgress.fade(
        specObject: JsonObject,
    ) = remember { FadeModifier(this, specObject) }

    class FadeModifier(
        private val animationProgress: AnimationProgress,
        specObject: JsonObject
    ) : ModifierTransition() {

        private val spec = Spec.from(specObject)
        private val startValue: Float
        private val endValue: Float

        init {
            val directionScreen = DirectionScreen.from(specObject)
            startValue = when (directionScreen) {
                DirectionScreen.Enter -> spec.alphaInitial
                DirectionScreen.Exit -> 1.0f
            }
            endValue = when (directionScreen) {
                DirectionScreen.Enter -> 1.0f
                DirectionScreen.Exit -> spec.alphaInitial
            }
        }

        @Composable
        override fun Modifier.animate(boundaries: Size): Modifier {
            val progress = animationProgress.animateFloat(
                startValue = startValue,
                endValue = endValue,
                animationSpec = tween(
                    durationMillis = spec.duration,
                    easing = LinearEasing
                )
            )
            return alpha(progress.value)
        }
    }

}