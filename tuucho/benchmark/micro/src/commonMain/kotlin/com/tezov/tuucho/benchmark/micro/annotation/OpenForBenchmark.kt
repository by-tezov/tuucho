@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.benchmark.micro.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class OpenForBenchmark
