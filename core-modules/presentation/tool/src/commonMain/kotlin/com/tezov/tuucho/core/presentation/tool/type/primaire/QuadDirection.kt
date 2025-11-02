package com.tezov.tuucho.core.presentation.tool.type.primaire

data class QuadDirection<T : Any?, S : Any?, B : Any?, E : Any?>(
    var top: T,
    var start: S,
    var bottom: B,
    var end: E,
)
