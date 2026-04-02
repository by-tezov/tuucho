package com.tezov.tuucho.benchmark

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.TearDown
import kotlinx.benchmark.Warmup
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Threads
import org.openjdk.jmh.annotations.Timeout
import java.util.concurrent.TimeUnit

@Fork(4)
@BenchmarkMode(Mode.AverageTime)
@Timeout(time = 10, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.SECONDS)
@Threads(value = 3)
@Warmup(iterations = 3, time = 100, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@State(Scope.Benchmark)
class MyBenchmark2 {

    @Param("0")
    var size = 10

    private val list = ArrayList<Int>()

    @Setup
    fun prepare() {
        for (i in 0..<size) {
            list.add(i)
        }
    }

    @TearDown
    fun cleanup() {
        list.clear()
    }

    @Benchmark
    fun benchmarkMethod(): Int {
        return list.sum()
    }

    @Benchmark
    fun iterateBenchmark(blackhole: Blackhole) {
        for (e in list) {
            blackhole.consume(e)
        }
    }

}
