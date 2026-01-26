@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui._system

import androidx.compose.runtime.staticCompositionLocalOf
import org.koin.core.Koin

val LocalTuuchoKoin = staticCompositionLocalOf<Koin> {
    error("not initialized")
}
