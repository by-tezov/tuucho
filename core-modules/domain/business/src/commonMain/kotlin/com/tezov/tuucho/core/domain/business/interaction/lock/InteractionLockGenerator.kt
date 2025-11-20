package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator.Input
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class InteractionLockGenerator(
    private val idGenerator: IdGeneratorProtocol<Unit, String>,
) : IdGeneratorProtocol<Input, InteractionLockGenerator.Lock.Element>,
    TuuchoKoinComponent {
    sealed class Lock(
        val owner: String
    ) {
        class Element(
            owner: String,
            val canBeRelease: Boolean = true,
            val value: String,
            val type: Type
        ) : Lock(owner) {
            override fun equals(
                other: Any?
            ): Boolean {
                if (this === other) return true
                if (other !is Element) return false
                return type == other.type && value == other.value
            }

            override fun hashCode(): Int {
                var result = type.hashCode()
                result = 31 * result + value.hashCode()
                return result
            }
        }

        class ElementArray(
            owner: String,
            val locks: List<Element>
        ) : Lock(owner)
    }

    data class Input(
        val owner: String,
        val type: Type,
    )

    override fun generate(
        input: Input
    ) = Lock.Element(
        owner = input.owner,
        type = input.type,
        value = idGenerator.generate()
    )

    override fun generate() = throw DomainException.Default("use generate(input) instead")
}
