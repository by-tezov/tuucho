package com.tezov.tuucho.core.presentation.tool.animation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tezov.tuucho.core.domain.tool.async.Notifier

class AnimationProgress private constructor() {

    private enum class Step { Start_Idle, Running, End_Idle }

    private lateinit var isStarted: MutableState<Boolean>
    private lateinit var transitionState: MutableTransitionState<Step>
    private lateinit var transition: Transition<Step>

    private val _events = Notifier.Emitter<Boolean>()
    val events get() = _events.createCollector

    val isIdle
        get() = !isStarted.value && (transitionState.currentState == Step.Start_Idle || transitionState.currentState == Step.End_Idle)

    fun start() {
        if (isStarted.value) return
        transitionState.targetState = Step.Start_Idle
        isStarted.value = true
    }

    private fun onDone() {
        _events.tryEmit(true)
    }

    @Composable
    private fun updateTransition() {
        isStarted = remember {
            mutableStateOf(false)
        }
        transitionState = remember {
            mutableStateOf(
                MutableTransitionState(Step.Start_Idle)
            )
        }.value
        with(transitionState) {
            if (isIdle) {
                when (currentState) {
                    Step.Start_Idle -> {
                        if (isStarted.value) {
                            targetState = Step.Running
                        }
                    }

                    Step.Running -> {
                        targetState = Step.End_Idle
                    }

                    Step.End_Idle -> {
                        if (isStarted.value) {
                            isStarted.value = false
                            onDone()
                        }
                    }
                }
            }
        }
        transition = updateTransition(transitionState = transitionState, label = "")
    }

    @Composable
    fun animateFloat(
        startValue: Float = START_VALUE,
        endValue: Float = END_VALUE,
        animationSpecToEnd: FiniteAnimationSpec<Float> = remember {
            spring()
        },
    ): State<Float> {
        val clampedValue = remember {
            mutableStateOf(startValue)
        }
        val animatedFloat = transition.animateFloat(
            transitionSpec = {
                when (transitionState.targetState) {
                    Step.Start_Idle, Step.End_Idle -> snap(delayMillis = 0)
                    else -> animationSpecToEnd
                }
            },
            label = "",
        ) {
            when (it) {
                Step.Start_Idle -> startValue
                else -> endValue
            }
        }
        clampedValue.value = when (transitionState.targetState) {
            Step.Running -> animatedFloat.value
            Step.Start_Idle -> startValue
            Step.End_Idle -> endValue
        }
        return clampedValue
    }

    companion object {

        private const val START_VALUE = 0.0f
        private const val END_VALUE = 1.0f

        @Composable
        fun updateAnimationProgress() = remember {
            AnimationProgress()
        }.also {
            it.updateTransition()
        }

    }

}