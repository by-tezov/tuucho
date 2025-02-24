package com.tezov.tuucho.domain.repository

import com.tezov.tuucho.domain.model.PageDomainObject

interface PageRepository {

    suspend fun retrieve(name: String): PageDomainObject

}