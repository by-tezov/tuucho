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
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.Spec.Value.DirectionNavigation
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.Spec.Value.DirectionScreen
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.SpecSlide.Value.Effect
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.SpecSlide.Value.Entrance
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.transition._system.ModifierTransition
import kotlinx.serialization.json.JsonObject

object TransitionSlideHorizontal {

    class Spec(
        val duration: Int,
        val exitDarkAlphaFactor: Float,
        val entrance: String,
        val effect: String,
    ) {
        companion object {
            fun from(specObject: JsonObject?) = specObject
                ?.withScope(SettingNavigationTransitionSchema.SpecSlide::Scope)
                .let {
                    Spec(
                        duration = it?.duration?.toIntOrNull() ?: 5000,
                        exitDarkAlphaFactor = it?.exitDarkAlphaFactor?.toFloatOrNull() ?: 0.8f,
                        entrance = it?.entrance ?: Entrance.fromEnd,
                        effect = it?.effect ?: Effect.coverPush,
                    )
                }
        }
    }

    @Composable
    fun AnimationProgress.slideHorizontal(
        specObject: JsonObject,
    ) = remember {
        val directionScreen = specObject
            .withScope(SettingNavigationTransitionSchema.Spec::Scope)
            .directionScreen

        val directionNavigation = specObject
            .withScope(SettingNavigationTransitionSchema.Spec::Scope)
            .directionNavigation

        when (directionScreen) {
            DirectionScreen.enter -> when (directionNavigation) {
                DirectionNavigation.forward -> FlatSlideModifier(
                    spec = Spec.from(specObject),
                    animationProgress = this,
                    directionNavigation = directionNavigation
                )

                DirectionNavigation.backward -> LayerSlideModifier(
                    spec = Spec.from(specObject),
                    animationProgress = this,
                    directionNavigation = directionNavigation
                )

                else -> throw UiException.Default("unknown direction navigation $directionNavigation")
            }

            DirectionScreen.exit -> when (directionNavigation) {
                DirectionNavigation.forward -> LayerSlideModifier(
                    spec = Spec.from(specObject),
                    animationProgress = this,
                    directionNavigation = directionNavigation
                )

                DirectionNavigation.backward -> FlatSlideModifier(
                    spec = Spec.from(specObject),
                    animationProgress = this,
                    directionNavigation = directionNavigation
                )

                else -> throw UiException.Default("unknown direction navigation $directionNavigation")
            }

            else -> throw UiException.Default("unknown direction screen $directionScreen")
        }
    }

    class FlatSlideModifier(
        private val spec: Spec,
        private val animationProgress: AnimationProgress,
        private val directionNavigation: String,
    ) : ModifierTransition() {

        private val startValue = when (directionNavigation) {
            DirectionNavigation.forward -> 1.0f
            DirectionNavigation.backward -> 0.0f
            else -> throw UiException.Default("unknown direction navigation $directionNavigation")
        }
        private val endValue = when (directionNavigation) {
            DirectionNavigation.forward -> 0.0f
            DirectionNavigation.backward -> 1.0f
            else -> throw UiException.Default("unknown direction navigation $directionNavigation")
        }

        private val entranceFactor = when (spec.entrance) {
            Entrance.fromEnd -> 1.0f
            Entrance.fromStart -> -1.0f
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
            if(boundaries == Size.Unspecified){
                return when (directionNavigation) {
                    DirectionNavigation.forward -> alpha(0.0f)
                    DirectionNavigation.backward -> this
                    else -> throw UiException.Default("unknown entrance value ${spec.entrance}")
                }
            }
            else {
                val width = boundaries.width.dp * entranceFactor
                return offset(x = width * progress.value, y = 0.dp)
            }
        }
    }

    class LayerSlideModifier(
        private val spec: Spec,
        private val animationProgress: AnimationProgress,
        private val directionNavigation: String,
    ) : ModifierTransition() {

        private val startValue = when (directionNavigation) {
            DirectionNavigation.forward -> 0.0f
            DirectionNavigation.backward -> -1.0f
            else -> throw UiException.Default("unknown direction navigation $directionNavigation")
        }
        private val endValue = when (directionNavigation) {
            DirectionNavigation.forward -> -1.0f
            DirectionNavigation.backward -> 0.0f
            else -> throw UiException.Default("unknown direction navigation $directionNavigation")
        }

        private val entranceFactor = when (spec.entrance) {
            Entrance.fromEnd -> 1.0f
            Entrance.fromStart -> -1.0f
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
            if(boundaries == Size.Unspecified){
                return when (directionNavigation) {
                    DirectionNavigation.forward -> this
                    DirectionNavigation.backward -> alpha(0.0f)
                    else -> throw UiException.Default("unknown entrance value ${spec.entrance}")
                }
            }
            else {
                val width = when (spec.effect) {
                    Effect.coverPush -> (boundaries.width.dp * entranceFactor) / 2
                    Effect.push -> (boundaries.width.dp * entranceFactor)
                    Effect.cover -> 0.dp
                    else -> throw UiException.Default("unknown effect value ${spec.effect}")
                }
                return offset(x = width * progress.value, y = 0.dp)
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