package com.tezov.tuucho.core.data.repository.mock

import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol
import dev.mokkery.answering.calls
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.JsonObject

fun mockCoroutineContext(
    currentScope: CoroutineScope
) = mock<CoroutineContextProtocol> {
    @Suppress("ktlint:standard:max-line-length")
    everySuspend {
        await(block = any<suspend CoroutineScope.() -> JsonObject>())
    } calls { (block: suspend CoroutineScope.() -> JsonObject) ->
        block(currentScope)
    }
}
