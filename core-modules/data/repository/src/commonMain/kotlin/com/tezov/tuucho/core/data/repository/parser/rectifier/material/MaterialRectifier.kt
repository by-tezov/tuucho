package com.tezov.tuucho.core.data.repository.parser.rectifier.material

import com.tezov.tuucho.core.data.repository.di.rectifier.RectifierModule
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.component.ComponentRectifier
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

@OpenForTest
@OptIn(TuuchoExperimentalAPI::class)
internal class MaterialRectifier : TuuchoKoinScopeComponent {
    override val scope: Scope by lazy {
        createScope(this)
    }

    private val componentRectifier: ComponentRectifier by inject()
    private val rectifiers: List<AbstractRectifier> by inject(RectifierModule.Name.RECTIFIERS)

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        materialObject: JsonObject
    ) = materialObject
        .withScope(MaterialSchema::Scope)
        .apply {
            rootComponent?.let {
                rootComponent = componentRectifier.process(ROOT_PATH, it).jsonObject
            }
            rectifiers.forEach { rectifier ->
                this[rectifier.key]?.let { this[rectifier.key] = rectifier.process(ROOT_PATH, it).jsonArray }
            }
        }.collect()
}
