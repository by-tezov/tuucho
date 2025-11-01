package com.tezov.tuucho.core.domain.business.protocol.repository

interface InterractionLockRepositoryProtocol {
    enum class Type {
        Navigation
    }

    suspend fun tryLock(
        type: Type
    ): String?

    suspend fun unLock(
        type: Type,
        handle: String
    )
}
