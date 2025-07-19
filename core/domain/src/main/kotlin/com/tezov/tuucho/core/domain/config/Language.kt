package com.tezov.tuucho.core.domain.config

enum class Language(val code: String) {
    Default("default"),
    French("fr");

    companion object {
        fun fromOrNull(value: String) =
            entries.firstOrNull { value == it.code }

        fun from(value: String) =
            entries.first { value == it.code }
    }

}