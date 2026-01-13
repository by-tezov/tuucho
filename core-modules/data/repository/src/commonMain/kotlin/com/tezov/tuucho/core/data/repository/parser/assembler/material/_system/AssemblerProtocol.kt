@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.assembler.material._system

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface AssemblerProtocol : AssemblerMatcherProtocol {
    val schemaType: String

    suspend fun process(
        path: JsonElementPath,
        element: JsonElement,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol
    ): JsonElement
}
