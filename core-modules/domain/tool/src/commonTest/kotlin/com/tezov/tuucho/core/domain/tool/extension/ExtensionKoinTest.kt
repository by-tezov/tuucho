package com.tezov.tuucho.core.domain.tool.extension

import com.tezov.tuucho.core.domain.tool.extension.ExtensionKoin.bindOrdered
import com.tezov.tuucho.core.domain.tool.extension.ExtensionKoin.getAllOrdered
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ExtensionKoinTest {
    interface SimpleProtocol

    class SimpleA : SimpleProtocol

    class SimpleB : SimpleProtocol

    class SimpleC : SimpleProtocol

    class SimpleD : SimpleProtocol

    interface AnotherProtocol

    class Another : AnotherProtocol

    @Test
    fun `resolve all - single module`() {
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
    fun `resolve all - multiple modules`() {
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
    fun `resolve all - mix of single and factory`() {
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
        val result2 = koin.getAll<SimpleProtocol>()

        assertEquals(3, result1.size)
        assertEquals(setOf("SimpleA", "SimpleB", "SimpleC"), names1.toSet())

        val factoryInstancesDiffer = result1.zip(result2).any { (a, b) -> a !== b }
        assertEquals(true, factoryInstancesDiffer)
    }

    @Test
    fun `resolve all - multiple types`() {
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

        assertEquals(setOf("SimpleA", "SimpleB", "SimpleC", "SimpleD"), names.toSet())

        // this is not guaranteed to pass since order is not documented but I keep it to warn me if it fail.
        assertNotEquals(listOf("SimpleC", "SimpleD", "SimpleA", "SimpleB"), names)
        assertEquals(listOf("SimpleA", "SimpleB", "SimpleC", "SimpleD"), names)
    }

    @Test
    fun `ordered - resolve all - single module - registration order`() {
        fun <T> List<T>.permutations(): Sequence<List<T>> = sequence {
            if (size <= 1) {
                yield(this@permutations)
            } else {
                for (i in indices) {
                    val element = this@permutations[i]
                    val rest = this@permutations.take(i) + this@permutations.drop(i + 1)
                    for (perm in rest.permutations()) yield(listOf(element) + perm)
                }
            }
        }

        val singles: List<Pair<String, Module.() -> Unit>> = listOf(
            "SimpleA" to { single { SimpleA() } bindOrdered SimpleProtocol::class },
            "SimpleB" to { single { SimpleB() } bindOrdered SimpleProtocol::class },
            "SimpleC" to { single { SimpleC() } bindOrdered SimpleProtocol::class }
        )
        val permutations = singles.permutations()

        assertEquals(6, permutations.count()) // "Expected 6 permutations for 3 elements"
        assertEquals(6, permutations.distinct().count()) // "Permutations must be unique"

        permutations.forEach { order ->
            val koin = koinApplication {
                modules(
                    module {
                        order.forEach { entry -> entry.second(this) }
                    }
                )
            }.koin

            val result = koin.getAllOrdered<SimpleProtocol>()
            val names = result.map { it::class.simpleName }

            assertEquals(order.map { it.first }, names)
        }
    }

    @Test
    fun `ordered - multiple modules - overwrite previous`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bindOrdered SimpleProtocol::class
                    single { SimpleB() } bindOrdered SimpleProtocol::class
                },
                // Second module will overwrite first ordered declaration, not an issue for me.
                module {
                    single { SimpleC() } bindOrdered SimpleProtocol::class
                    single { SimpleD() } bindOrdered SimpleProtocol::class
                }
            )
        }.koin

        val result = koin.getAllOrdered<SimpleProtocol>()
        val names = result.map { it::class.simpleName }

        assertEquals(2, result.size)
        assertEquals(setOf("SimpleC", "SimpleD"), names.toSet())
    }

    @Test
    fun `ordered - mix with normal bind`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bindOrdered SimpleProtocol::class
                    single { SimpleB() } bind SimpleProtocol::class
                    single { SimpleC() } bindOrdered SimpleProtocol::class
                    single { SimpleD() } bind SimpleProtocol::class
                }
            )
        }.koin

        val result = koin.getAllOrdered<SimpleProtocol>()
        val names = result.map { it::class.simpleName }

        // getAllOrdered retrieve only those bind with bindOrdered
        assertEquals(2, result.size)
        assertEquals(setOf("SimpleA", "SimpleC"), names.toSet())
    }

    @Test
    fun `ordered - multiple types are isolated`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bindOrdered SimpleProtocol::class
                    single { SimpleB() } bindOrdered SimpleProtocol::class
                    single { Another() } bindOrdered AnotherProtocol::class
                }
            )
        }.koin

        val simple = koin.getAllOrdered<SimpleProtocol>()
        val another = koin.getAllOrdered<AnotherProtocol>()

        assertEquals(listOf("SimpleA", "SimpleB"), simple.map { it::class.simpleName })
        assertEquals(listOf("Another"), another.map { it::class.simpleName })
    }

    @Test
    fun `ordered - supports factory definitions`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bindOrdered SimpleProtocol::class
                    factory { SimpleB() } bindOrdered SimpleProtocol::class
                    single { SimpleC() } bindOrdered SimpleProtocol::class
                }
            )
        }.koin

        val first = koin.getAllOrdered<SimpleProtocol>()
        val second = koin.getAllOrdered<SimpleProtocol>()

        assertEquals(listOf("SimpleA", "SimpleB", "SimpleC"), first.map { it::class.simpleName })
        assertEquals(listOf("SimpleA", "SimpleB", "SimpleC"), second.map { it::class.simpleName })
        assertNotEquals(first[1], second[1])
    }
}
