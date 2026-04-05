package com.tezov.tuucho.benchmark

import com.tezov.tuucho.benchmark.annotation.OpenForBenchmark
import com.tezov.tuucho.core.barrel._system.CoroutineScopes
import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.di.rectifier.MaterialRectifierScope
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.config.ConfigRectifier
import com.tezov.tuucho.core.domain.business._system.IdGenerator
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business._system.koin.koinApplication
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import com.tezov.tuucho.core.domain.tool.json.InstantSerializer
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
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import org.koin.core.Koin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.onClose
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Threads
import org.openjdk.jmh.annotations.Timeout
import java.util.concurrent.TimeUnit
import kotlin.time.Instant
import com.tezov.tuucho.uiComponent.stable.di.MaterialRectifierModule as UiStableMaterialRectifierModule

@OpenForBenchmark
@State(Scope.Benchmark)
class RectifierBenchmarkState {

    @Param(
        "page-login.json",
        "page-home.json",
    )
    lateinit var url: String

    var koin: Koin? = null
    var materialObject: JsonObject? = null

    private fun getJsonObject(jsonResourceName: String): JsonObject {
        val jsonConverter = koin!!.get<Json>()
        val jsonResourceString = Thread.currentThread()
            .contextClassLoader
            .getResource("json/$jsonResourceName")
            ?.readText()
            ?: throw IllegalArgumentException("Resource not found: $jsonResourceName")
        return jsonConverter.decodeFromString(
            deserializer = JsonObject.serializer(),
            string = jsonResourceString
        )
    }

    @OptIn(TuuchoInternalApi::class)
    @Setup
    fun setup() {
        koin = koinApplication(
            koinMassModules = listOf(
                module(ModuleContextData.Main) {
                    single<Json> {
                        Json {
                            ignoreUnknownKeys = true
                            encodeDefaults = true
                            explicitNulls = true
                            serializersModule = SerializersModule {
                                contextual(Instant::class, InstantSerializer())
                            }
                        }
                    }
                    single {
                        CoroutineScopes(exceptionMonitor = null)
                    } bind CoroutineScopesProtocol::class onClose { coroutineScopes ->
                        coroutineScopes?.cancel()
                    }
                    singleOf(::IdGenerator) bind IdGeneratorProtocol::class // <Unit, String>
                },
                module(ModuleContextData.Rectifier) {
                    singleOf(::MaterialRectifier) onClose { rectifier ->
                        rectifier?.closeScope()
                    }
                    factoryOf(::ConfigRectifier)
                },
                MaterialRectifierScope.invoke(),
                UiStableMaterialRectifierModule.invoke()
            ),
            extension = null
        ).koin
        materialObject = getJsonObject(url)
    }


    @TearDown
    fun teardown() {
        koin?.close()
        koin = null
        materialObject = null
    }

}

@OpenForBenchmark
@Threads(value = 1)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@Timeout(time = 20, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 1, time = 5, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 10, time = 2, timeUnit = BenchmarkTimeUnit.SECONDS)
@Suppress("unused")
class RectifierBenchmark {

    @Benchmark
    fun readPage(
        state: RectifierBenchmarkState,
        blackhole: Blackhole
    ) = runBlocking {
        val materialRectifier = state.koin!!.get<MaterialRectifier>()
        val result = materialRectifier.process(
            context = RectifierProtocol.Context(
                url = state.url
            ),
            materialObject = state.materialObject!!
        )
        blackhole.consume(result)
    }

}
