package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.model.image.ImageModel
import kotlinx.coroutines.flow.Flow

interface ImageRepositoryProtocol {
    interface Image<S : Any> {
        val source: S
        val tags: Set<String>?
        val tagsExcluder: Set<String>?
        val size: Long
        val width: Int
        val height: Int
    }

    fun <S : Any> process(
        models: List<ImageModel>
    ): Flow<Image<S>>
}
