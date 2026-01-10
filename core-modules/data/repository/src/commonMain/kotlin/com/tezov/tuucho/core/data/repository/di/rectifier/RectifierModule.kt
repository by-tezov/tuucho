package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import org.koin.core.module.dsl.singleOf

internal object RectifierModule {
    fun invoke() = module(ModuleGroupData.Rectifier) {
        singleOf(::MaterialRectifier)
        singleOf(::ResponseRectifier)
    }
}
