package com.tezov.tuucho.domain.usecase

import com.tezov.tuucho.domain.model.PageDomainObject
import com.tezov.tuucho.domain.repository.PageRepository

class GetPageObjectUseCase(private val repository: PageRepository) {

    suspend operator fun invoke(name: String): PageDomainObject = repository.retrieve(name)

}