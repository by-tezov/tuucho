package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.module.dsl.singleOf

internal object RectifierModule {
    fun invoke() = module(ModuleContextData.Rectifier) {
        singleOf(::MaterialRectifier)
        singleOf(::ResponseRectifier)
    }
}
