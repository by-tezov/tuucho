package com.tezov.tuucho.core.domain.business.model

data class LanguageModelDomain(
    val code: String?,
    val country: String?
) {
    val tag get() = country?.let { "$code-$country" }
}
