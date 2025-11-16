package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import java.lang.ref.WeakReference

@OpenForTest
class InteractionLockGenerator(
    private val idGenerator: IdGeneratorProtocol<Unit, String>,
) : IdGeneratorProtocol<Type, InteractionLockGenerator.Lock.Element>,
    TuuchoKoinComponent {
    private val lockRepositoryRef: WeakReference<InteractionLockRepositoryProtocol> by lazy {
        WeakReference(getKoin().get<InteractionLockRepositoryProtocol>())
    }

    sealed class Lock {
        abstract suspend fun isValid(): Boolean

        abstract suspend fun release()

        data class Element(
            private val lockRepositoryRef: WeakReference<InteractionLockRepositoryProtocol>,
            val canBeRelease: Boolean = true,
            val value: String,
            val type: Type
        ) : Lock() {
            override suspend fun isValid(): Boolean = lockRepositoryRef.get()?.isValid(this) ?: false

            override suspend fun release() {
                if (!canBeRelease) return
                lockRepositoryRef.get()?.release(this)
            }

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

        data class ElementArray(
            val locks: List<Element>
        ) : Lock() {
            override suspend fun isValid(): Boolean = locks.all { it.isValid() }

            override suspend fun release() = locks.forEach { it.release() }
        }
    }

    override fun generate(
        input: Type
    ) = Lock.Element(
        lockRepositoryRef = lockRepositoryRef,
        type = input,
        value = idGenerator.generate()
    )

    override fun generate() = throw DomainException.Default("use generate(input) instead")
}
