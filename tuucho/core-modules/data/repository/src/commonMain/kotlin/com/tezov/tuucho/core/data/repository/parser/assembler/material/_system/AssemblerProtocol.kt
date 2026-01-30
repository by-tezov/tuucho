@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.assembler.material._system

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface AssemblerProtocol : AssemblerMatcherProtocol {
    val schemaType: String

    data class Context(
        val url: String,
        val findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol,
    ) {
        companion object {
            fun Context.toContextRectifier() = RectifierProtocol.Context(
                url = url
            )
        }
    }

    suspend fun process(
        context: Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement
}
