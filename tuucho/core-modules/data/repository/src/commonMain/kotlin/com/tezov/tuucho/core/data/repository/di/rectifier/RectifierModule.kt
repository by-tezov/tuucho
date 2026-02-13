package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.config.ConfigRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.dsl.onClose
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

internal object RectifierModule {
    fun invoke() = module(ModuleContextData.Rectifier) {
        single<MaterialRectifier>() onClose { rectifier ->
            rectifier?.closeScope()
        }
        single<ResponseRectifier>() onClose { rectifier ->
            rectifier?.closeScope()
        }
        factory<ConfigRectifier>()
    }
}
