package com.tezov.tuucho.core.presentation.tool.animation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.coroutines.channels.BufferOverflow

class AnimationProgress private constructor() {

    private enum class Step { Initial, Head, Running, Tail }

    private val transitionState = mutableStateOf(MutableTransitionState(Step.Initial))
    private lateinit var transition: Transition<Step>

    private val _events = Notifier.Emitter<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events get() = _events.createCollector

    val isIdle get() = transitionState.value.isIdle && transitionState.value.targetState != Step.Running

    fun start() {
        if (!isIdle) return
        transitionState.value.targetState = Step.Head
    }

    private fun onDone() {
        _events.tryEmit(Unit)
    }

    @Composable
    private fun rememberTransition() {
        transition = rememberTransition(transitionState = transitionState.value)
        with(transitionState.value) {
            when (currentState) {
                Step.Initial -> {}
                Step.Head -> targetState = Step.Running
                Step.Running -> if(isIdle) {
                    targetState = Step.Tail
                }
                Step.Tail -> onDone()
            }
        }
    }

    @Composable
    fun animateFloat(
        startValue: Float = START_VALUE,
        endValue: Float = END_VALUE,
        animationSpec: FiniteAnimationSpec<Float> = remember {
            spring()
        },
    ): State<Float> {
        val clampedValue = remember { mutableStateOf(startValue) }
        val animatedFloat = transition.animateFloat(
            transitionSpec = {
                when (transitionState.value.targetState) {
                    Step.Initial, Step.Head, Step.Tail -> snap(delayMillis = 0)
                    else -> animationSpec
                }
            },
        ) {
            when (it) {
                Step.Initial, Step.Head -> startValue
                else -> endValue
            }
        }
        clampedValue.value = when (transitionState.value.targetState) {
            Step.Running -> animatedFloat.value
            Step.Initial, Step.Head -> startValue
            Step.Tail -> endValue
        }
        return clampedValue
    }

    companion object {
        private const val START_VALUE = 0.0f
        private const val END_VALUE = 1.0f

        @Composable
        fun rememberAnimationProgress() = remember { AnimationProgress() }
            .also { it.rememberTransition() }

        @Composable
        fun rememberAnimationProgress(key1: Any?) = remember(key1) { AnimationProgress() }
            .also { it.rememberTransition() }
    }

}