package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.data.parser._system.toPath
import com.tezov.tuucho.core.domain.model.material.MaterialModelDomain
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialAssembler(
    private val database: Database,
    private val json: Json
) : KoinComponent {

    private val componentAssembler: ComponentAssembler by inject()

    suspend fun decode(
        config: ExtraDataAssembler
    ): MaterialModelDomain? {
        val versioning = database.versioning()
            .find(url = config.url) ?: return null
        versioning.rootPrimaryKey ?: return null
        val entity = database.jsonEntity().find(versioning.rootPrimaryKey) ?: return null

//        database.jsonEntity().selectAll().filter { it.type == "text" }.forEach {
//            println(it)
//        }
//        println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")


        println(entity.jsonObject)
        val jsonElementDecoded = componentAssembler
            .process(
                path = "".toPath(),
                element = entity.jsonObject,
                extraData = config
            )
        println(jsonElementDecoded)

//        val componentDecoded = json.decodeFromJsonElement(
//            ComponentModelDomain.PolymorphicSerializer,
//            jsonElementDecoded
//        )
        return MaterialModelDomain(root = TODO())
    }
}