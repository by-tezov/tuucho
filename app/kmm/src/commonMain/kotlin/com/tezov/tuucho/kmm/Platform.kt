package com.tezov.tuucho.kmm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform