package com.tezov.tuucho.convention.project._system

fun optIn() = listOf(
    "kotlin.uuid.ExperimentalUuidApi",
    "kotlin.ExperimentalUnsignedTypes",
    "kotlin.time.ExperimentalTime",
    "kotlin.concurrent.atomics.ExperimentalAtomicApi",
//            "kotlin.ExperimentalMultiplatform",
).asIterable()
