package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.config.Language

class GetLanguageUseCase {

    fun invoke(): Language =
        Language.Default //TODO retrieve system language or preference application
}