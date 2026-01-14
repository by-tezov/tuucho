@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.bindOrdered
import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.getAllOrdered
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BindOrderedTest {
    interface SimpleProtocol

    class SimpleA : SimpleProtocol

    class SimpleB : SimpleProtocol

    class SimpleC : SimpleProtocol

    class SimpleD : SimpleProtocol

    interface AnotherProtocol

    class Another : AnotherProtocol

    @Test
    fun `getAllOrdered - no ordered bindings returns empty list`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bind SimpleProtocol::class
                }
            )
        }.koin

        val result = koin.getAllOrdered<SimpleProtocol>()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `getAllOrdered - only one ordered bindings`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bindOrdered SimpleProtocol::class
                }
            )
        }.koin

        val result = koin.getAllOrdered<SimpleProtocol>()

        assertEquals(listOf("SimpleA"), result.map { it::class.simpleName })
    }

    @Test
    fun `getAllOrdered - single module - registration order`() {
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
    fun `getAllOrdered - multiple modules - overwrite previous`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bindOrdered SimpleProtocol::class
                    single { SimpleB() } bindOrdered SimpleProtocol::class
                },
                // Second module will overwrite first ordered declaration, not an issue for me.
                module {
                    single { SimpleD() } bindOrdered SimpleProtocol::class
                    single { SimpleB() } bindOrdered SimpleProtocol::class
                }
            )
        }.koin

        val result = koin.getAllOrdered<SimpleProtocol>()
        val names = result.map { it::class.simpleName }

        assertEquals(2, result.size)
        assertEquals(setOf("SimpleD", "SimpleB"), names.toSet())
    }

    @Test
    fun `getAllOrdered - multiple modules - overwrite previous - side effect`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } bindOrdered SimpleProtocol::class
                    single { SimpleB() } bindOrdered SimpleProtocol::class
                    single { SimpleC() } bindOrdered SimpleProtocol::class
                },
                // Second module will overwrite first ordered declaration, not an issue for me.
                // but if first module had more then as side effect, all index above from previous module are present
                module {
                    single { SimpleD() } bindOrdered SimpleProtocol::class
                    single { SimpleB() } bindOrdered SimpleProtocol::class
                }
            )
        }.koin

        val result = koin.getAllOrdered<SimpleProtocol>()
        val names = result.map { it::class.simpleName }

        assertEquals(3, result.size)
        assertEquals(
            setOf(
                "SimpleD",
                "SimpleB",
                "SimpleC" // side effect from first module
            ),
            names.toSet()
        )
    }

    @Test
    fun `getAllOrdered - mix with normal bind`() {
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
    fun `getAllOrdered - multiple types are isolated`() {
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
    fun `getAllOrdered - supports factory definitions`() {
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
