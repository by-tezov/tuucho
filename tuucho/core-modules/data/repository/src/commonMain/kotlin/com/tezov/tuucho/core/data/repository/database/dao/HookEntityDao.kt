package com.tezov.tuucho.core.data.repository.database.dao

import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.data.repository.database.entity.HookEntity
import com.tezov.tuucho.core.data.repository.database.entity.toEntity
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
internal class HookQueries(
    private val database: Database
) {
    private val queries get() = database.hookStatementQueries

    fun selectAll() = queries.selectAll().executeAsList().map { it.toEntity() }

    fun deleteAll() = queries.deleteAll()

    fun delete(
        url: String
    ) {
        queries.deleteByUrl(url)
    }

    fun insert(
        entity: HookEntity
    ) {
        queries.insert(
            url = entity.url,
            rootPrimaryKey = entity.rootPrimaryKey,
            visibility = entity.visibility,
            lifetime = entity.lifetime,
        )
    }

    fun getOrNull(
        url: String
    ) = queries.getByUrl(url).executeAsOneOrNull()?.toEntity()

    fun getLifetimeOrNull(
        url: String
    ) = queries.getLifetimeByUrl(url).executeAsOneOrNull()
}
