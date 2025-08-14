package com.tezov.tuucho.core.presentation.ui.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.presentation.ui.animation._system.ModifierAnimation

class AnimationNone : ModifierAnimation() {

    @Composable
    override fun Modifier.animate() = this
}