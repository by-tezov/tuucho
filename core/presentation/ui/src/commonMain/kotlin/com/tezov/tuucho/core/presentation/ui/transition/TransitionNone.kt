package com.tezov.tuucho.core.presentation.ui.transition

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.presentation.ui.transition._system.ModifierAnimation

class TransitionNone : ModifierAnimation() {

    @Composable
    override fun Modifier.animate() = this
}