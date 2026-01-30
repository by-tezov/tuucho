@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class KoinCoreTest {
    interface SimpleProtocol

    class SimpleA : SimpleProtocol

    class SimpleB : SimpleProtocol

    class SimpleC : SimpleProtocol

    class SimpleD : SimpleProtocol

    interface AnotherProtocol

    class Another : AnotherProtocol

    @Test
    fun `getAll - single module`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bind SimpleProtocol::class
                    single { SimpleB() } bind SimpleProtocol::class
                    single { SimpleC() } bind SimpleProtocol::class
                }
            )
        }.koin

        val result = koin.getAll<SimpleProtocol>()
        val names = result.map { it::class.simpleName }

        assertEquals(3, result.size)
        assertEquals(setOf("SimpleA", "SimpleB", "SimpleC"), names.toSet())
    }

    @Test
    fun `getAll - multiple modules`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bind SimpleProtocol::class
                },
                module {
                    single { SimpleB() } bind SimpleProtocol::class
                },
                module {
                    single { SimpleC() } bind SimpleProtocol::class
                }
            )
        }.koin

        val result = koin.getAll<SimpleProtocol>()
        val names = result.map { it::class.simpleName }

        assertEquals(3, result.size)
        assertEquals(setOf("SimpleA", "SimpleB", "SimpleC"), names.toSet())
    }

    @Test
    fun `getAll - mix of single and factory`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bind SimpleProtocol::class
                    factory { SimpleB() } bind SimpleProtocol::class
                    single { SimpleC() } bind SimpleProtocol::class
                }
            )
        }.koin
        val result1 = koin.getAll<SimpleProtocol>()
        val names1 = result1.map { it::class.simpleName }

        assertEquals(3, result1.size)
        assertEquals(setOf("SimpleA", "SimpleB", "SimpleC"), names1.toSet())

        val result2 = koin.getAll<SimpleProtocol>()
        val factoryInstancesDiffer = result1.zip(result2).filter { (a, b) -> a === b }
        assertEquals(2, factoryInstancesDiffer.size)
    }

    @Test
    fun `getAll - multiple types`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bind SimpleProtocol::class
                    single { SimpleB() } bind SimpleProtocol::class
                    single { Another() } bind AnotherProtocol::class
                }
            )
        }.koin

        val resultSimple = koin.getAll<SimpleProtocol>()
        val resultAnother = koin.getAll<AnotherProtocol>()

        assertEquals(2, resultSimple.size)
        assertEquals(1, resultAnother.size)
    }

    @Test
    fun `getAll is not ordered`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleC() } bind SimpleProtocol::class
                    single { SimpleD() } bind SimpleProtocol::class
                    single { SimpleA() } bind SimpleProtocol::class
                    single { SimpleB() } bind SimpleProtocol::class
                }
            )
        }.koin

        val result = koin.getAll<SimpleProtocol>()
        val names = result.map { it::class.simpleName }

        // this is not guaranteed to pass since order is not documented but I keep it to warn me if it fail.
        assertNotEquals(listOf("SimpleC", "SimpleD", "SimpleA", "SimpleB"), names)
        assertEquals(listOf("SimpleB", "SimpleD", "SimpleC", "SimpleA"), names)
    }
}
