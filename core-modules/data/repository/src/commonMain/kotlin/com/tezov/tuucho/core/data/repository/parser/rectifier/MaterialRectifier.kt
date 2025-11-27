package com.tezov.tuucho.core.data.repository.parser.rectifier

import com.tezov.tuucho.core.data.repository.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.component.ComponentRectifier
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

@OpenForTest
@OptIn(TuuchoExperimentalAPI::class)
internal class MaterialRectifier : TuuchoKoinComponent {
    private val componentRectifier: ComponentRectifier by inject()
    private val rectifiers: List<AbstractRectifier> by inject(Name.RECTIFIERS)

    suspend fun process(
        materialObject: JsonObject
    ) = materialObject
        .withScope(MaterialSchema::Scope)
        .apply {
            rootComponent?.let {
                rootComponent = componentRectifier.process("".toPath(), it).jsonObject
            }
            rectifiers.forEach { rectifier ->
                this[rectifier.key]?.let { this[rectifier.key] = rectifier.process("".toPath(), it).jsonArray }
            }
        }.collect()
}
