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
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.Spec.Value.DirectionScreen
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.ui.exception.UiException
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
    ) = remember {
        val directionScreen = specObject
            .withScope(SettingNavigationTransitionSchema.Spec::Scope)
            .directionScreen
        FadeModifier(Spec.from(specObject), this, directionScreen)
    }

    class FadeModifier(
        private val spec: Spec,
        private val animationProgress: AnimationProgress,
        directionScreen: String?,
    ) : ModifierTransition() {

        private val startValue = when (directionScreen) {
            DirectionScreen.enter -> spec.alphaInitial
            DirectionScreen.exit -> 1.0f
            else -> throw UiException.Default("unknown direction screen $directionScreen")
        }
        private val endValue = when (directionScreen) {
            DirectionScreen.enter -> 1.0f
            DirectionScreen.exit -> spec.alphaInitial
            else -> throw UiException.Default("unknown direction screen $directionScreen")
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