package com.tezov.tuucho.data.network.repository

import com.tezov.tuucho.data.network.service.PageNetworkService

class PageRepositoryImpl(private val service: PageNetworkService): PageRepository   {

    suspend fun retrieve(name: String): PageDomainObject {
        return service.retrieve(name).toDomain()
    }

}