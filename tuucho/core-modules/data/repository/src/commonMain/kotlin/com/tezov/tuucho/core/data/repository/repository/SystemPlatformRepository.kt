package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository._system.SystemPlatformInformationProtocol
import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol

internal class SystemPlatformRepository(
    private val keyValueStorage: KeyValueStoreRepositoryProtocol,
    private val systemPlatformInformation: SystemPlatformInformationProtocol,
) : SystemPlatformRepositoryProtocol {

    override suspend fun getCurrentLanguage(): LanguageModelDomain {
        var language = keyValueStorage.getOrNull(KeyValueStoreRepository.language)?.value
        var country = keyValueStorage.getOrNull(KeyValueStoreRepository.country)?.value
        if (language != null) {
            return LanguageModelDomain(language, country)
        }
        language = systemPlatformInformation.currentLanguage().also {
            keyValueStorage.save(KeyValueStoreRepository.language, it.toValue())
        }
        country = systemPlatformInformation.currentCountry()?.also {
            keyValueStorage.save(KeyValueStoreRepository.country, it.toValue())
        }
        return LanguageModelDomain(language, country)
    }

    override suspend fun setCurrentLanguage(value: LanguageModelDomain) {
        keyValueStorage.save(KeyValueStoreRepository.language, value.code?.toValue())
        keyValueStorage.save(KeyValueStoreRepository.country, value.country?.toValue())
    }
}
