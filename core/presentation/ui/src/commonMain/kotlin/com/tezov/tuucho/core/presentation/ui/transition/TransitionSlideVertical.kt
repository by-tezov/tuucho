package com.tezov.tuucho.core.presentation.ui.transition

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.SpecSlide.Value.Effect
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.SpecSlide.Value.Entrance
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.transition._system.AbstractModifierTransition
import com.tezov.tuucho.core.presentation.ui.transition._system.DirectionNavigation
import com.tezov.tuucho.core.presentation.ui.transition._system.DirectionScreen
import kotlinx.serialization.json.JsonObject

object TransitionSlideVertical {

    class Spec(
        val duration: Int,
        val exitDarkAlphaFactor: Float,
        val entrance: String,
        val effect: String,
    ) {
        companion object {
            fun from(specObject: JsonObject?) = specObject
                ?.withScope(SettingComponentNavigationTransitionSchema.SpecSlide::Scope)
                .let {
                    Spec(
                        duration = it?.duration?.toIntOrNull() ?: 350,
                        exitDarkAlphaFactor = it?.exitDarkAlphaFactor?.toFloatOrNull() ?: 0.75f,
                        entrance = it?.entrance ?: Entrance.fromBottom,
                        effect = it?.effect ?: Effect.coverPush,
                    )
                }
        }
    }

    @Composable
    fun AnimationProgress.slideVertical(
        specObject: JsonObject,
    ) = remember {
        val directionScreen = DirectionScreen.from(specObject)
        val directionNavigation = DirectionNavigation.from(specObject)

        when (directionScreen) {
            DirectionScreen.Enter -> when (directionNavigation) {
                DirectionNavigation.Forward -> FlatSlideModifier(
                    animationProgress = this,
                    specObject = specObject
                )

                DirectionNavigation.Backward -> LayerSlideModifier(
                    animationProgress = this,
                    specObject = specObject
                )
            }

            DirectionScreen.Exit -> when (directionNavigation) {
                DirectionNavigation.Forward -> LayerSlideModifier(
                    animationProgress = this,
                    specObject = specObject
                )

                DirectionNavigation.Backward -> FlatSlideModifier(
                    animationProgress = this,
                    specObject = specObject
                )
            }
        }

    }

    class FlatSlideModifier(
        private val animationProgress: AnimationProgress,
        specObject: JsonObject,
    ) : AbstractModifierTransition() {

        private val spec = Spec.from(specObject)
        val directionNavigation = DirectionNavigation.from(specObject)

        private val startValue = when (directionNavigation) {
            DirectionNavigation.Forward -> 1.0f
            DirectionNavigation.Backward -> 0.0f
        }
        private val endValue = when (directionNavigation) {
            DirectionNavigation.Forward -> 0.0f
            DirectionNavigation.Backward -> 1.0f
        }

        private val entranceFactor = when (spec.entrance) {
            Entrance.fromBottom -> 1.0f
            Entrance.fromTop -> -1.0f
            else -> throw UiException.Default("unknown entrance value ${spec.entrance}")
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
            if (boundaries == Size.Unspecified) {
                return when (directionNavigation) {
                    DirectionNavigation.Forward -> alpha(0.0f)
                    DirectionNavigation.Backward -> this
                }
            } else {
                val height = boundaries.height.dp * entranceFactor
                return offset(x = 0.dp, y = height * progress.value)
            }
        }
    }

    class LayerSlideModifier(
        private val animationProgress: AnimationProgress,
        specObject: JsonObject,
    ) : AbstractModifierTransition() {

        private val spec = Spec.from(specObject)
        val directionNavigation = DirectionNavigation.from(specObject)

        private val startValue = when (directionNavigation) {
            DirectionNavigation.Forward -> 0.0f
            DirectionNavigation.Backward -> -1.0f
        }
        private val endValue = when (directionNavigation) {
            DirectionNavigation.Forward -> -1.0f
            DirectionNavigation.Backward -> 0.0f
        }

        private val entranceFactor = when (spec.entrance) {
            Entrance.fromBottom -> 1.0f
            Entrance.fromTop -> -1.0f
            else -> throw UiException.Default("unknown entrance value ${spec.entrance}")
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
            if (boundaries == Size.Unspecified) {
                return when (directionNavigation) {
                    DirectionNavigation.Forward -> this
                    DirectionNavigation.Backward -> alpha(0.0f)
                }
            } else {
                val height = when (spec.effect) {
                    Effect.coverPush -> (boundaries.height.dp * entranceFactor) / 4
                    Effect.push -> (boundaries.height.dp * entranceFactor)
                    Effect.cover -> 0.dp
                    else -> throw UiException.Default("unknown effect value ${spec.effect}")
                }
                return this
                    .offset(x = 0.dp, y = height * progress.value)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            color = Color.Black,
                            alpha = -progress.value * spec.exitDarkAlphaFactor,
                            topLeft = Offset(0f, 0f),
                            size = size,
                            style = Fill
                        )
                    }
            }
        }
    }

}