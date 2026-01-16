package com.tezov.tuucho.core.domain.business.interaction.lock

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator.Input
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
internal class InteractionLockGenerator(
    private val idGenerator: IdGeneratorProtocol<Unit, String>,
) : IdGeneratorProtocol<Input, InteractionLock>,
    TuuchoKoinComponent {
    data class Input(
        val owner: String,
        val type: InteractionLockType,
    )

    override fun generate(
        input: Input
    ) = InteractionLock(
        owner = input.owner,
        type = input.type,
        id = idGenerator.generate()
    )

    override fun generate() = throw DomainException.Default("use generate(input) instead")
}
