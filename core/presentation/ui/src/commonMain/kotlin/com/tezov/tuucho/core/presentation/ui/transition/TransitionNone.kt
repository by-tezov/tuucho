package com.tezov.tuucho.core.presentation.ui.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import com.tezov.tuucho.core.presentation.tool.animation.AnimationProgress
import com.tezov.tuucho.core.presentation.ui.transition._system.ModifierTransition

object TransitionNone {

    @Composable
    fun AnimationProgress.none() = remember { NoneModifier() }

    class NoneModifier() : ModifierTransition() {
        @Composable
        override fun Modifier.animate(boundaries: Size) = this
    }

}