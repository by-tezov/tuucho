package com.tezov.tuucho.core.domain.repository

import com.tezov.tuucho.core.domain.model.material.MaterialModelDomain

interface MaterialRepository {

    suspend fun refreshCache(url: String)

    suspend fun retrieve(url: String): MaterialModelDomain

}