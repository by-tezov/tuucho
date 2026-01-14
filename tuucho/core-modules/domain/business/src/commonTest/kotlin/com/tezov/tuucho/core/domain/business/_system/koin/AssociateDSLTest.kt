@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.koin

import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.declaration
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business.exception.DomainException
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class AssociateDSLTest {
    interface Marker

    interface MarkerOther

    interface SimpleProtocol

    class SimpleA : SimpleProtocol

    class SimpleB : SimpleProtocol

    class SimpleC : SimpleProtocol

    class SimpleD : SimpleProtocol

    @Test
    fun `associate - adds secondary type`() {
        val module = module {
            single { SimpleA() }
        }

        val factory = module.declaration<SimpleA>()
        factory.associate(Marker::class)

        assertEquals(
            listOf(Marker::class),
            factory.beanDefinition.secondaryTypes
        )
    }

    @Test
    fun `declaration - returns instance factory`() {
        val module = module {
            single { SimpleA() }
        }

        val factory = module.declaration<SimpleA>()

        assertEquals(SimpleA::class, factory.beanDefinition.primaryType)
    }

    @Test
    fun `declaration - throws when missing`() {
        val module = module {}
        assertFailsWith<DomainException> {
            module.declaration<SimpleA>()
        }
    }

    @Test
    fun `getAllAssociated - no associate returns empty list`() {
        val koin = koinApplication {}.koin

        val result = koin.getAllAssociated<SimpleProtocol>(Marker::class)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `getAllAssociated - only one associate bindings`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } associate Marker::class
                }
            )
        }.koin

        val result = koin.getAllAssociated<SimpleProtocol>(Marker::class)

        assertEquals(listOf("SimpleA"), result.map { it::class.simpleName })
    }

    @Test
    fun `associate - multiple associate to same type`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } associate Marker::class
                    single { SimpleB() } associate Marker::class
                }
            )
        }.koin

        val result = koin.getAllAssociated<SimpleProtocol>(Marker::class)

        assertEquals(setOf("SimpleA", "SimpleB"), result.map { it::class.simpleName }.toSet())
    }

    @Test
    fun `getAllAssociated - ignores non associated bindings`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } associate Marker::class
                    single { SimpleB() }
                }
            )
        }.koin

        val result = koin.getAllAssociated<SimpleProtocol>(Marker::class)

        assertEquals(listOf("SimpleA"), result.map { it::class.simpleName })
    }

    @Test
    fun `getAllAssociated - is not ordered`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleC() } associate Marker::class
                    single { SimpleD() } associate Marker::class
                    single { SimpleA() } associate Marker::class
                    single { SimpleB() } associate Marker::class
                }
            )
        }.koin

        val result = koin.getAllAssociated<SimpleProtocol>(Marker::class)
        val names = result.map { it::class.simpleName }

        // this is not guaranteed to pass since order is not documented but I keep it to warn me if it fail.
        assertNotEquals(listOf("SimpleC", "SimpleD", "SimpleA", "SimpleB"), names)
        assertEquals(listOf("SimpleA", "SimpleB", "SimpleC", "SimpleD"), names)
    }

    @Test
    fun `getAllAssociated - factory instances are recreated`() {
        val koin = koinApplication {
            modules(
                module {
                    factory { SimpleA() } associate Marker::class
                }
            )
        }.koin

        val first = koin.getAllAssociated<SimpleProtocol>(Marker::class).first()
        val second = koin.getAllAssociated<SimpleProtocol>(Marker::class).first()

        assertNotEquals(first, second)
    }

    @Test
    fun `scope getAllAssociated - no associate returns empty list`() {
        val koin = koinApplication {
            modules(
                module {
                    scope(named("empty")) {
                        scoped { SimpleA() }
                    }
                }
            )
        }.koin
        val scope = koin.createScope("emptyScope", named("empty"))

        val result = scope.getAllAssociated<SimpleProtocol>(Marker::class)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `scope associate - only one associate bindings`() {
        val koin = koinApplication {
            modules(
                module {
                    scope(named("test")) {
                        scoped { SimpleA() } associate Marker::class
                    }
                }
            )
        }.koin
        val scope = koin.createScope("test", named("test"))

        val result = scope.getAllAssociated<SimpleProtocol>(Marker::class)

        assertEquals(listOf("SimpleA"), result.map { it::class.simpleName })
    }

    @Test
    fun `scope getAllAssociated - multiple associate to same type`() {
        val koin = koinApplication {
            modules(
                module {
                    scope(named("multi")) {
                        scoped { SimpleA() } associate Marker::class
                        scoped { SimpleB() } associate Marker::class
                        scoped { SimpleC() } associate Marker::class
                    }
                }
            )
        }.koin
        val scope = koin.createScope("multiScope", named("multi"))

        val result = scope.getAllAssociated<SimpleProtocol>(Marker::class)

        assertEquals(
            setOf("SimpleA", "SimpleB", "SimpleC"),
            result.map { it::class.simpleName }.toSet()
        )
    }

    @Test
    fun `getAllAssociated - multiple different markers are isolated`() {
        val koin = koinApplication {
            modules(
                module {
                    single { SimpleA() } associate MarkerOther::class
                    single { SimpleB() } associate Marker::class
                }
            )
        }.koin

        val result1 = koin.getAllAssociated<SimpleProtocol>(MarkerOther::class)
        val result2 = koin.getAllAssociated<SimpleProtocol>(Marker::class)

        assertEquals(listOf("SimpleA"), result1.map { it::class.simpleName })
        assertEquals(listOf("SimpleB"), result2.map { it::class.simpleName })
    }

    @Test
    fun `scope getAllAssociated - multiple different markers are isolated`() {
        val koin = koinApplication {
            modules(
                module {
                    scope(named("test")) {
                        scoped { SimpleA() } associate MarkerOther::class
                        scoped { SimpleB() } associate Marker::class
                    }
                }
            )
        }.koin
        val scope = koin.createScope("testScope", named("test"))

        val result1 = scope.getAllAssociated<SimpleProtocol>(MarkerOther::class)
        val result2 = scope.getAllAssociated<SimpleProtocol>(Marker::class)

        assertEquals(listOf("SimpleA"), result1.map { it::class.simpleName })
        assertEquals(listOf("SimpleB"), result2.map { it::class.simpleName })
    }
}
