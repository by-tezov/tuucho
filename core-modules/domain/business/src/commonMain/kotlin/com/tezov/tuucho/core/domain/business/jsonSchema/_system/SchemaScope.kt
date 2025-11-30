@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.jsonSchema._system

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <T : OpenSchemaScope<T>> JsonElement.withScope(
    scopeFactory: (argument: SchemaScopeArgument) -> T,
    mapOperator: OpenSchemaScope.MapOperator? = null,
): T {
    contract {
        callsInPlace(scopeFactory, InvocationKind.EXACTLY_ONCE)
    }
    return scopeFactory(
        SchemaScopeArgument(
            element = this,
            moveOnRoot = false,
            mapOperator = mapOperator
        )
    )
}

@OptIn(ExperimentalContracts::class)
fun <T : OpenSchemaScope<T>> JsonElement.onScope(
    scopeFactory: (argument: SchemaScopeArgument) -> T,
): T {
    contract {
        callsInPlace(scopeFactory, InvocationKind.EXACTLY_ONCE)
    }
    return scopeFactory(
        SchemaScopeArgument(
            element = this,
            moveOnRoot = true,
            mapOperator = null
        )
    )
}

data class SchemaScopeArgument(
    val element: JsonElement,
    val moveOnRoot: Boolean,
    val mapOperator: OpenSchemaScope.MapOperator?,
)

class SchemaScope(
    argument: SchemaScopeArgument,
) : OpenSchemaScope<SchemaScope>(argument)

open class OpenSchemaScope<T : OpenSchemaScope<T>>(
    private val argument: SchemaScopeArgument,
) {

    interface MapOperator : DelegateSchemaKey.MapOperator {
        val hasBeenChanged: Boolean

        val element: JsonElement

        fun contains(
            key: String
        ): Boolean

        fun remove(
            key: String
        )

        fun collect(): JsonObject
    }

    open val root: String = ""

    private var _mapOperator: MapOperator? = null

    protected val mapOperator
        get() = _mapOperator ?: run {
            (argument.mapOperator ?: SchemaScopeMapOperator(
                root = root,
                argument = argument
            )).also { _mapOperator = it }
        }

    val element get() = mapOperator.element

    val hasBeenChanged get() = mapOperator.hasBeenChanged

    fun contains(
        key: String
    ) = mapOperator.contains(key)

    operator fun get(
        key: String
    ): JsonElement? = mapOperator.read(key)

    operator fun set(
        key: String,
        value: JsonElement
    ) {
        mapOperator.write(key, value)
    }

    fun keys(): Set<String> = (element as? JsonObject)?.keys ?: emptySet()

    fun remove(
        key: String
    ) {
        mapOperator.remove(key)
    }

    protected inline fun <reified T : Any?> delegate(
        key: String? = null,
    ) = DelegateSchemaKey<T>(mapOperator, T::class, key)

    fun <T : OpenSchemaScope<T>> withScope(
        scopeFactory: (argument: SchemaScopeArgument) -> T
    ) = element.withScope(scopeFactory, mapOperator)

    fun <T : OpenSchemaScope<T>> onScope(
        scopeFactory: (argument: SchemaScopeArgument) -> T
    ) = element.onScope(scopeFactory)

    fun collect() = mapOperator.collect()

    fun collectChangedOrNull() = if (hasBeenChanged) mapOperator.collect() else null

    override fun toString(): String = "initial=${element}\ncurrent=${collect()}"
}
