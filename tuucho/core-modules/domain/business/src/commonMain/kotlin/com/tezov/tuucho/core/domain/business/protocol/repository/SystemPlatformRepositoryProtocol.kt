package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain

interface SystemPlatformRepositoryProtocol {
    suspend fun getCurrentLanguage(): LanguageModelDomain

    suspend fun setCurrentLanguage(
        value: LanguageModelDomain
    )
}
